package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static GP.MutationOperators.SVMFieldAccessExpr.getFieldAccessExpr;
import static GP.MutationOperators.SVMMethodCall.getMethodCallExpr;
import static GP.MutationOperators.SVMObjectCreation.getObjectCreationExpr;
import static GP.MutationOperators.SVMVariableDeclarator.getVariableDeclarator;

public final class SVM {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        /* (1) -- Collect all listed nodes from the compilation unit */
        // - Variable Declarations
        // - Method Calls
        // - Object Instantiations
        // - Field Access Call
        List<Node> nodes = new ArrayList<>(collectExpressions(program));
        System.out.println("NODES: " + nodes);

        /* (2) -- Get random node from subset */
        if (nodes.size() == 0) { throw new Exception("None of the required nodes could be found in the Compilation Unit"); }
        Node nodeFrom = nodes.get(GPHelpers.randomIndex(nodes.size()));
        Node nodeTo = null;

        /* (3) -- Determine the node to change to */
        System.out.println(nodeFrom.getClass().getSimpleName());
        switch (nodeFrom.getClass().getSimpleName()) {
            case "VariableDeclarator": // WORKS PRETTY WELL! (Replaces variable initialiser with a different value)
                nodeTo = getVariableDeclarator(nodeFrom, ((VariableDeclarator) nodeFrom).resolve().getType().describe());
                break;
            case "MethodCallExpr": // WORKS PRETTY WELL! (Replaces with a method of same return type in CU)
                nodeTo = getMethodCallExpr(nodeFrom, ((MethodCallExpr) nodeFrom).resolve().getReturnType().describe(), program);
                break;
            case "ObjectCreationExpr": // WORKS PRETTY WELL! (Replaces object with overloaded constructor with different params)
                nodeTo = getObjectCreationExpr(nodeFrom, program);
                break;
            case "FieldAccessExpr": // WORKS PRETTY WELL! ()
                nodeTo = getFieldAccessExpr(nodeFrom, ((FieldAccessExpr) nodeFrom).resolve().getType().describe());
                break;
        }

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }

        /* Print and return */
        System.out.println(program);
        return program.clone();
    }

    public static boolean compareLineNumbers(Optional<Position> position, Optional<Position> nodeDeclarationPosition) {
        if(!position.isPresent() || !nodeDeclarationPosition.isPresent()) { return false; }

        return position.get().line < nodeDeclarationPosition.get().line;
    }

    private static List<Node> collectExpressions(CompilationUnit cu) {
        List<Node> nodes = new ArrayList<>();
        nodes.addAll(cu.findAll(MethodCallExpr.class));
        nodes.addAll(cu.findAll(FieldAccessExpr.class));
        nodes.addAll(cu.findAll(ObjectCreationExpr.class));
        nodes.addAll(cu.findAll(VariableDeclarator.class));

        return nodes;
    }

    public static List<String> getMethodParams(ResolvedMethodDeclaration method) {
        List <String> params = new ArrayList<>();
        for (int i = 0; i < method.getNumberOfParams(); i++) {
            params.add(method.getParam(i).getType().describe());
        }

        return params;
    }

    public static List<String> getConstructorParams(ResolvedConstructorDeclaration constructor) {
        List <String> params = new ArrayList<>();
        for (int i = 0; i < constructor.getNumberOfParams(); i++) {
            params.add(constructor.getParam(i).getType().describe());
        }

        return params;
    }

    public static NodeList<Expression> getRequiredTypes(Node node, List<String> params) {
        NodeList<Expression> arguments = new NodeList<>();

        for (String param : params) {
            List<Expression> resolvedNodes = getExpressionsInSpecificClassAndMethod(node, param);
            resolvedNodes.remove(node);
            if (resolvedNodes.size() == 0) { return null; }
            arguments.add(resolvedNodes.get(GPHelpers.randomIndex(resolvedNodes.size())));
        }

        return arguments;
    }

    public static List<Expression> getExpressionsInSpecificClassAndMethod(Node node, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();

        node.findAncestor(ClassOrInterfaceDeclaration.class).ifPresent(coid -> {
            // Get all field variables
            coid.findAll(FieldDeclaration.class).forEach(fd -> fd.getVariables().forEach(vd -> {
                if (vd.resolve().getType().describe().equals(resolvedType)) {
                    expressions.add(vd.getNameAsExpression().clone());
                }
            }));

            // Get enum declarations
            coid.findAll(EnumDeclaration.class).forEach(ed -> {
                if (ed.resolve().getClassName().equals(resolvedType)) {
                    System.out.println(ed.getNameAsExpression());
                    for (EnumConstantDeclaration enumConstant : ed.getEntries()) {
                        // Create FieldAccessExpr and give enum constant value e.g. Person.ALIVE;
                        expressions.add(new FieldAccessExpr().setScope(ed.getNameAsExpression()).setName(enumConstant.getName()));
                    }
                }
            });
        });


        node.findAncestor(MethodDeclaration.class).ifPresent(md -> {
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
}