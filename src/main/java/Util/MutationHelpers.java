package Util;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import java.util.*;

public final class MutationHelpers {
    public static final BinaryExpr.Operator[] relationOperators = new BinaryExpr.Operator[]{ BinaryExpr.Operator.LESS, BinaryExpr.Operator.LESS_EQUALS, BinaryExpr.Operator.GREATER, BinaryExpr.Operator.GREATER_EQUALS, BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS };
    public static final BinaryExpr.Operator[] booleanOperators  = new BinaryExpr.Operator[]{ BinaryExpr.Operator.OR, BinaryExpr.Operator.AND};
    public static final UnaryExpr.Operator[] unaryOperators  = new UnaryExpr.Operator[]{ UnaryExpr.Operator.LOGICAL_COMPLEMENT};

    public static int randomIndex(int size) {
        if (size == 1) return 0;
        return new Random().nextInt(size);
    }

    public static List<String> getMethodParams(ResolvedMethodDeclaration method) {
        List<String> params = new ArrayList<>();
        for (int i = 0; i < method.getNumberOfParams(); i++) {
            params.add(method.getParam(i).getType().describe());
        }

        return params;
    }

    public static List<String> getConstructorParams(ResolvedConstructorDeclaration constructor) {
        List<String> params = new ArrayList<>();
        for (int i = 0; i < constructor.getNumberOfParams(); i++) {
            params.add(constructor.getParam(i).getType().describe());
        }

        return params;
    }

    public static boolean compareLineNumbers(Optional<Position> position, Optional<Position> nodeDeclarationPosition) {
        if (!position.isPresent() || !nodeDeclarationPosition.isPresent()) {
            return false;
        }

        return position.get().line < nodeDeclarationPosition.get().line;
    }

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

    private static boolean methodImplemented(MethodDeclaration md) {
        if (!md.findAncestor(ClassOrInterfaceDeclaration.class).get().isInterface() && !md.findAncestor(ClassOrInterfaceDeclaration.class).get().isAbstract()) { return true; }
        return (md.resolve().isStatic());
    }

    public static List<Expression> resolveCollection(Node node, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();

        expressions.addAll(resolveLocalTypes(node, resolvedType));
        expressions.addAll(resolveMethodDeclarations(node, resolvedType));
        expressions.addAll(resolveObjectCreationExpr(node, resolvedType));
        expressions.addAll(resolveFieldAccessExpr(node, resolvedType));

        System.out.println("EXPRESSIONS: " + expressions);
        return expressions;
    }

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

    public static List<Expression> resolveObjectCreationExpr(Node node, String className) {
        List<Expression> expressions = new ArrayList<>();

        node.findCompilationUnit().flatMap(cu -> cu.getClassByName(className)).ifPresent(i -> i.getConstructors().forEach(constructor -> {
            // Fill the parameters for each constructor
            ObjectCreationExpr newOCE = new ObjectCreationExpr().setType(className);
            NodeList<Expression> arguments = MutationHelpers.getRequiredTypes(node, MutationHelpers.getConstructorParams(constructor.resolve()));

            if (arguments != null) {
                newOCE.setArguments(arguments);
                expressions.add(newOCE);
            }
        }));

        return expressions;
    }

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
}