package Util;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import java.util.*;

/**
 * The MutationHelpers class provides helper functions for collecting nodes and types of the given AST program.
 */
public final class MutationHelpers {
    public static final BinaryExpr.Operator[] relationOperators = new BinaryExpr.Operator[]{ BinaryExpr.Operator.LESS, BinaryExpr.Operator.LESS_EQUALS, BinaryExpr.Operator.GREATER, BinaryExpr.Operator.GREATER_EQUALS, BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS };
    public static final BinaryExpr.Operator[] booleanOperators  = new BinaryExpr.Operator[]{ BinaryExpr.Operator.OR, BinaryExpr.Operator.AND};
    public static final UnaryExpr.Operator[] unaryOperators  = new UnaryExpr.Operator[]{ UnaryExpr.Operator.LOGICAL_COMPLEMENT};
    public static final String[] binaryExpressionAllowedMethods = new String[] { "contains", "startsWith", "endsWith", "equalsIgnoreCase"};

    /**
     * Returns a random index from a given range.
     *
     * @param size  A positive int value that represents the range of possible random values that can be chosen from
     * @return      A positive random integer from the given range
     */
    public static int randomIndex(int size) {
        if (size == 1) return 0;
        return new Random().nextInt(size);
    }

    /**
     * The type parameters of a Method are returned.
     *
     * @param method    The MethodDeclaration for the method we want the parameters for
     * @return          A List of Strings containing the String type of each parameter in the method declaration
     */
    public static List<String> getMethodParams(ResolvedMethodDeclaration method) {
        List<String> params = new ArrayList<>();
        for (int i = 0; i < method.getNumberOfParams(); i++) {
            params.add(method.getParam(i).getType().describe());
        }

        return params;
    }

    /**
     * The type parameters of a Constructor are returned.
     *
     * @param constructor   The ResolvedConstructorDeclaration for the constructor we want the parameters for
     * @return              A List of Strings containing the String type of each parameter in the constructor declaration
     */
    public static List<String> getConstructorParams(ResolvedConstructorDeclaration constructor) {
        List<String> params = new ArrayList<>();
        for (int i = 0; i < constructor.getNumberOfParams(); i++) {
            params.add(constructor.getParam(i).getType().describe());
        }

        return params;
    }

    /**
     * Determines if a node is above or below another node to restrict the amount of nodes that are returned.
     *
     * @param position                  A position object that holds the line number for the node we are trying to collect
     * @param nodeDeclarationPosition   A position object that holds the line number for the fixed node we are comparing against
     * @return                          A boolean type, true if the node we are interested occurs before the other; false otherwise
     */
    public static boolean compareLineNumbers(Optional<Position> position, Optional<Position> nodeDeclarationPosition) {
        if (!position.isPresent() || !nodeDeclarationPosition.isPresent()) {
            return false;
        }

        return position.get().line < nodeDeclarationPosition.get().line;
    }

    /**
     * Given a list of parameters find replacements for these parameters of the same type. Given a node that represents the node in the AST program find expressions in the local scope that are acceptable.
     *
     * @param node      The chosen node that we need to use for line checking
     * @param params    A list of types as Strings that we need to find replacement expressions for
     * @return          A nodelist of expressions with the same length as the params list
     */
    public static NodeList<Expression> getRequiredTypes(Node node, List<String> params) {
        NodeList<Expression> arguments = new NodeList<>();

        for (String param : params) {
            List<Expression> resolvedNodes = resolveLocalTypes(node, param);
            resolvedNodes.remove(node);
            if (resolvedNodes.size() == 0) {
                return null;
            }
            arguments.add(resolvedNodes.get(MutationHelpers.randomIndex(resolvedNodes.size())));
        }

        return arguments;
    }

    /**
     * Ensure that the method declaration chosen has been implemented, checking interfaces and abstract classes for static methods.
     *
     * @param md    The method declaration that we are trying to determine if it has been implemented or not
     * @return      A boolean type determining if the method has been implemented or not
     */
    private static boolean methodImplemented(MethodDeclaration md) {
        if (!md.findAncestor(ClassOrInterfaceDeclaration.class).get().isInterface() && !md.findAncestor(ClassOrInterfaceDeclaration.class).get().isAbstract()) { return true; }
        return (md.resolve().isStatic());
    }

    /**
     * Resolve all nodes of a certain type in the local and global scope of a given node. Local scope checks the current class and field declaration.
     * While the global scope checks for method declarations, object creation expressions and field access expressions in other methods and classes.
     *
     * @param node              The node we determine a list of expressions for in the local and global scope
     * @param resolvedType      A String type for the expressions in scope we are trying to collect
     * @return                  A list of expressions of the required type that where found in the local and global scope of a given node
     */
    public static List<Expression> resolveCollection(Node node, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();

        expressions.addAll(resolveLocalTypes(node, resolvedType));
        expressions.addAll(resolveMethodDeclarations(node, resolvedType));
        expressions.addAll(resolveObjectCreationExpr(node, resolvedType));
        expressions.addAll(resolveFieldAccessExpr(node, resolvedType));

        return expressions;
    }

    /**
     * Resolve all expressions in the local scope of a given node including;
     *  - Enum declarations in the program
     *  - Field variables in the class
     *  - Method call expressions, Variable declarations, Object creation expressions and Field access expressions in the method
     *  - Method parameters of the method the node belongs to if applicable
     *
     * @param node              The node we determine a list of expressions for in the local and global scope
     * @param resolvedType      A String type for the expressions in scope we are trying to collect
     * @return                  A list of expressions of the required type that where found in the local scope of a given node
     */
    public static List<Expression> resolveLocalTypes(Node node, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();

        node.findCompilationUnit().ifPresent(cu -> {
            // Get enum declarations in Compilation Unit scope
            cu.findAll(EnumDeclaration.class).forEach(ed -> {
                if (ed.resolve().getClassName().equals(resolvedType)) {
                    for (EnumConstantDeclaration enumConstant : ed.getEntries()) {
                        // Create FieldAccessExpr and give enum constant value e.g. Person.ALIVE;
                        expressions.add(new FieldAccessExpr().setScope(ed.getNameAsExpression()).setName(enumConstant.getName()));
                    }
                }
            });
        });

        node.findAncestor(ClassOrInterfaceDeclaration.class).ifPresent(coid -> {
            // Get all field variables
            coid.findAll(FieldDeclaration.class).forEach(fd -> fd.getVariables().forEach(vd -> {
                if (vd.resolve().getType().describe().equals(resolvedType) && compareLineNumbers(vd.getBegin(), node.getBegin())) {
                    expressions.add(vd.getNameAsExpression().clone());
                }
            }));
        });

        node.findAncestor(MethodDeclaration.class).ifPresent(md -> {
            // Skip Method Declaration if method is an interface and not static or is abstract
            if (!md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent() || !methodImplemented(md)) { return; }

            // Get all method call expressions and check the return type-> person.getName()
            md.findAll(MethodCallExpr.class).forEach(mce -> {
                if (mce.resolve().getReturnType().describe().equals(resolvedType) && compareLineNumbers(mce.getBegin(), node.getBegin())) {
                    expressions.add(mce.clone());
                }
            });

            // Add all variable declarators, in Java a type must be declared, e.g. String name = "Fenton";
            md.findAll(VariableDeclarator.class).forEach(vd -> {
                if (vd.resolve().getType().describe().equals(resolvedType) && compareLineNumbers(vd.getBegin(), node.getBegin())) {
                    expressions.add(vd.getNameAsExpression().clone());
                }
            });

            // Add all object creation expr, e.g. new Person("Fenton");
            md.findAll(ObjectCreationExpr.class).forEach(oce -> {
                if (oce.resolve().getClassName().equals(resolvedType) && compareLineNumbers(oce.getBegin(), node.getBegin())) {
                    expressions.add(oce.clone());
                }
            });

            // Add all field access expressions
            md.findAll(FieldAccessExpr.class).forEach(fae -> {
                if (fae.resolve().getType().describe().equals(resolvedType) && compareLineNumbers(fae.getBegin(), node.getBegin())) {
                    expressions.add(fae.clone());
                }
            });

            // Add all method parameters
            md.getParameters().forEach(parameter -> {
                if (parameter.resolve().getType().describe().equals(resolvedType)) {
                    expressions.add(parameter.getNameAsExpression().clone());
                }
            });
        });

        return expressions;
    }

    /**
     * Resolve all method declarations in the program that new parameters of the same type can be generated for.
     * Method declarations must be implementable and accessible for them to be considered.
     * Methods in the same method and classes are considered as well as static methods and methods in other classes.
     *
     * @param node              The node we determine a list of expressions for based on the method declarations in the program
     * @param resolvedType      A String type for the expressions in scope we are trying to collect
     * @return                  A list of expressions of the required type that could be resolved in scope of the given node
     */
    public static List<Expression> resolveMethodDeclarations(Node node, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();
        String nodeMethodSignature = node.findAncestor(MethodDeclaration.class).isPresent() ? node.findAncestor(MethodDeclaration.class).get().resolve().getQualifiedSignature() : null;

        node.findCompilationUnit().ifPresent(cu -> {
            // Get all methods in the CompilationUnit with same return type and resolve parameters
            cu.findAll(MethodDeclaration.class).forEach(md -> {
                // If the return type of the method call matches and the signature must not equal the signature of the method the node is in, otherwise we could get a recursion issue
                if (md.resolve().getReturnType().describe().equals(resolvedType) && !md.resolve().getQualifiedSignature().equals(nodeMethodSignature) && methodImplemented(md)) {
                    // Create a method call expression and resolve types
                    MethodCallExpr currentMCE = new MethodCallExpr().setName(md.resolve().getName());
                    NodeList<Expression> arguments = getRequiredTypes(node, getMethodParams(md.resolve()));

                    if (arguments != null) {
                        currentMCE.setArguments(arguments);
                    } else {
                        return;
                    }

                    // If the methodDeclaration is a local method to nodeFrom e.g. TestMutation.method1() -> where nodeFrom is in TestMutation class
                    ClassOrInterfaceDeclaration coid = node.findAncestor(ClassOrInterfaceDeclaration.class).isPresent() ? node.findAncestor(ClassOrInterfaceDeclaration.class).get() : null;
                    if (coid == null) { return; }

                    String className = coid.resolve().getClassName();
                    if (className != null && md.resolve().getClassName().equals(className)) {
                        expressions.add(currentMCE);

                    // If the methodDeclaration is static then resolve via static methods
                    } else if (md.resolve().isStatic()) {
                        // Add Class or Interface name + method name
                        expressions.add(new FieldAccessExpr().setScope(new NameExpr(md.resolve().getClassName())).setName(String.valueOf(currentMCE)).clone());

                    // If the methodDeclaration is in another class e.g. Person.getAge() -> where nodeFrom is in TestMutation class
                    } else {
                        // Check for nodes in nodeFrom's local scope with className of required type e.g. Person
                        List<Expression> resolvedNodes = resolveLocalTypes(node, md.resolve().getClassName());

                        // If the required node was found in the local scope then create a fieldAccessExpr to combine both the node and method call expr e.g. new Person().getAge()
                        if (resolvedNodes.size() > 0) {
                                expressions.add(new FieldAccessExpr().setScope(resolvedNodes.get(MutationHelpers.randomIndex(resolvedNodes.size()))).setName(String.valueOf(currentMCE)));

                        // No object of type found in local scope, create a fieldAccessExpr using a constructor of required type e.g. new Person().getAge()
                        } else {
                            List<Expression> objectCreationExprs = resolveObjectCreationExpr(node, md.resolve().getClassName());

                            if (objectCreationExprs.size() > 0) {
                                expressions.add(new FieldAccessExpr().setScope(objectCreationExprs.get(MutationHelpers.randomIndex(objectCreationExprs.size()))).setName(String.valueOf(currentMCE)).clone());
                            }
                        }
                    }
                }
            });
        });

        return expressions;
    }

    /**
     * For a given class try and create all overloaded constructors filling in the parameters as required.
     * If no valid constructor exists for the class then the default constructor is used instead.
     *
     * @param node          The node we determine a list of object creation expressions for
     * @param className     The name of the class we are trying to find overloaded constructors for
     * @return              A list of expressions that contain each resolved constructor for the given className
     */
    public static List<Expression> resolveObjectCreationExpr(Node node, String className) {
        List<Expression> expressions = new ArrayList<>();

        node.findCompilationUnit().ifPresent(cu -> cu.getClassByName(className).ifPresent(i -> {
            // If the class has a default constructor
            if (i.getConstructors().size() >= 1) {
                i.getConstructors().forEach(constructor -> {
                    // Fill the parameters for each constructor
                    ObjectCreationExpr newOCE = new ObjectCreationExpr().setType(className);
                    NodeList<Expression> arguments = MutationHelpers.getRequiredTypes(node, MutationHelpers.getConstructorParams(constructor.resolve()));

                    if (arguments != null) {
                        newOCE.setArguments(arguments);
                        expressions.add(newOCE);
                    }
                });

            // If the class has no constructors then call the default constructor
            } else {
                expressions.add(new ObjectCreationExpr().setType(className));
            }
        }));

        return expressions;
    }

    /**
     * Resolve all field access expressions in the program.
     *
     * @param node              The node we determine a list of expressions for based on the field access expressions in the program
     * @param resolvedType      A String type for the expressions in scope we are trying to collect
     * @return                  A list of expressions for field access expressions that could be in different classes and methods
     */
    public static List<Expression> resolveFieldAccessExpr(Node node, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();

        node.findCompilationUnit().ifPresent(cu -> {
            cu.findAll(FieldDeclaration.class).forEach(fd -> {
                // If local to nodeFrom then ignore as localTypes will catch this
                String nodeClass = node.findAncestor(ClassOrInterfaceDeclaration.class).isPresent() ? node.findAncestor(ClassOrInterfaceDeclaration.class).get().resolve().getClassName() : null;
                String fdClass = fd.findAncestor(ClassOrInterfaceDeclaration.class).isPresent() ? fd.findAncestor(ClassOrInterfaceDeclaration.class).get().resolve().getClassName() : null;
                if (nodeClass == null || fdClass == null || nodeClass.equals(fdClass) || !fd.resolve().getType().describe().equals(resolvedType)) { return; }

                fd.getVariables().forEach(vd -> {
                    // If the fd is static or in an interface or abstract class
                    if (fd.isStatic() || fd.findAncestor(ClassOrInterfaceDeclaration.class).get().isInterface() || fd.findAncestor(ClassOrInterfaceDeclaration.class).get().isAbstract()) {
                        expressions.add(new FieldAccessExpr().setScope(new NameExpr(fdClass)).setName(vd.getNameAsString()).clone());
                        return;
                    }

                    // Try and find reference to fdClass in local scope
                    List<Expression> resolvedNodes = resolveLocalTypes(node, fdClass);
                    if (resolvedNodes.size() > 0) {
                        expressions.add(new FieldAccessExpr().setScope(resolvedNodes.get(MutationHelpers.randomIndex(resolvedNodes.size()))).setName(vd.getNameAsString()));

                    // Create a new Object Expr and prepend to field
                    } else {
                        List<Expression> objectCreationExprs = resolveObjectCreationExpr(node, fdClass);
                        if (objectCreationExprs.size() > 0) {
                            expressions.add(new FieldAccessExpr().setScope(objectCreationExprs.get(MutationHelpers.randomIndex(objectCreationExprs.size()))).setName(vd.getNameAsString()));
                        }
                    }
                });
            });
        });

        return expressions;
    }

    /**
     * Get all the types in a given program and return them as a HashMap of a type to expressions.
     *
     * @param node   The node we determine a list of expressions for based on the field access expressions in the program
     * @return       A HashMap of String types to a List of expressions that have that type
     */
    public static HashMap<String, List<Expression>> resolveAllTypes(Node node) {
        HashMap<String, List<Expression>> typeToExpressionMap = new HashMap<>();
        HashSet<String> cuTypes = new HashSet<>();

        // Add all class names, method return types for declarations and calls, variable declarations, enum declarations
        node.findCompilationUnit().ifPresent(cu -> {
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(coid -> cuTypes.add(coid.resolve().getClassName()));
            cu.findAll(ObjectCreationExpr.class).forEach(oce -> cuTypes.add(oce.resolve().getClassName()));
            cu.findAll(MethodDeclaration.class).forEach(md -> cuTypes.add(md.resolve().getReturnType().describe()));
            cu.findAll(MethodCallExpr.class).forEach(mce -> cuTypes.add(mce.resolve().getReturnType().describe()));
            cu.findAll(Parameter.class).forEach(parameter -> cuTypes.add(parameter.resolve().getType().describe()));
            cu.findAll(VariableDeclarator.class).forEach (vd -> cuTypes.add(vd.resolve().getType().describe()));
            cu.findAll(EnumDeclaration.class).forEach(ed -> cuTypes.add(ed.resolve().getClassName()));
            cu.findAll(FieldAccessExpr.class).forEach(fae -> cuTypes.add(fae.resolve().getType().describe()));
        });

        // Resolve all types
        for (String type : cuTypes) { typeToExpressionMap.put(type, resolveCollection(node, type)); }

        return typeToExpressionMap;
    }

    /**
     * Given a binary expression e.g. (x > 5 && y < 5) determine the child nodes of this, e.g. x > 5, y < 5.
     * Child nodes can be enclosed in brackets e.g. (x > 5) or unary e.g. !(x > 5).
     *
     * @param node   The node we wish to get the child nodes for, usually a binary expression is passed
     * @return       A list of nodes that when combined would create the parent node
     */
    public static List<Node> getChildrenOfExpression(Node node) {
        List<Node> expressions = new ArrayList<>();

        // If the node isn't a binary expression, enclosed expression or unary expression then add the child node, this is the base case
        if (!(node instanceof BinaryExpr) && !(node instanceof EnclosedExpr) && !(node instanceof UnaryExpr)) {
            expressions.add(node);
            return expressions;

        // If this is binary expression is not seperated by && or || then add the left and right nodes as we have children in those nodes
        } else if (node instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) node;

            if (!Arrays.asList(MutationHelpers.booleanOperators).contains(binaryExpr.getOperator())) {
                expressions.add(node);
                return expressions;
            }
        }

        // Get the children nodes and process them recursively until they reach a base case
        for (Node child : node.getChildNodes()) {
            expressions.addAll(getChildrenOfExpression(child));
        }

        return expressions;
    }

    /**
     * Statement expressions are ones that occur in if statements, while statements etc...
     * For this method only statements in if, while and for each statements, are collected as the expressions will be manipulated by various mutation operators.
     *
     * @param cu    The AST representation of the program
     * @return      A list of expressions that represent the expression of a control statement
     */
    public static List<Expression> collectStatementExpressions(CompilationUnit cu) {
        List<Expression> expressions = new ArrayList<>();

        // Collect if, while and ternary expressions
        cu.findAll(IfStmt.class).forEach(stmt -> expressions.add(stmt.getCondition()));
        cu.findAll(WhileStmt.class).forEach(stmt -> expressions.add(stmt.getCondition()));
        cu.findAll(ConditionalExpr.class).forEach(stmt -> expressions.add(stmt.getCondition()));

        // Get boolean variable types in fields and methods of the program
        cu.findAll(VariableDeclarationExpr.class).forEach(vde -> vde.getVariables().forEach(vd -> {
            if (vd.resolve().getType().describe().equals("boolean") && vd.getInitializer().isPresent()) {
                expressions.add(vd.getInitializer().get());
            }
        }));

        cu.findAll(FieldDeclaration.class).forEach(fd -> fd.getVariables().forEach(vd -> {
            if (vd.resolve().getType().describe().equals("boolean") && vd.getInitializer().isPresent()) {
                expressions.add(vd.getInitializer().get());
            }
        }));

        // Get return statements of methods that return a Boolean type
        cu.findAll(ReturnStmt.class).forEach(stmt -> {
            stmt.findAncestor(MethodDeclaration.class).ifPresent(md -> {
                if (md.resolve().getReturnType().describe().equals("boolean") && stmt.getExpression().isPresent()) {
                    expressions.add(stmt.getExpression().get());
                }
            });
        });

        return expressions;
    }
}