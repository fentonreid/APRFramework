package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.google.errorprone.annotations.Var;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SVM {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        /* (1) -- Collect all listed nodes from the compilation unit */
        // - Variable Declarations -> simple name
        // - Method calls
        // - Object creation expressions
        // - Enums
        List<Node> nodes = new ArrayList<>(collectExpressions(program));

        /* (2) -- Select from and to nodes */
        // a - SimpleName
        // b - Method Call Expression
        // c - Object Creation Expression
        // d - Enum

        Node nodeFrom = nodes.get(GPHelpers.randomIndex(nodes.size()));
        Node nodeTo = null;

        /* (3) -- Finding a switch */
        // Define resolved type of node
        System.out.println(nodeFrom.getClass().getSimpleName());
        switch (nodeFrom.getClass().getSimpleName()) {
            case "SimpleName":
                // Resolve the simple name type by finding the variable declarator
                List<String> simpleNameNode = new ArrayList<>();
                program.findAll(VariableDeclarator.class).forEach(vd -> { if (vd.getName().equals(nodeFrom)) { simpleNameNode.add(vd.resolve().getType().describe()); }});
                nodeTo = getVariableDeclarator(nodeFrom, simpleNameNode.get(0));
                break;
            case "MethodCallExpr":
                nodeTo = getMethodCallExpr(nodeFrom, ((MethodCallExpr) nodeFrom).resolve().getReturnType().describe());
                break;
            case "ObjectCreationExpr":
                nodeTo = getObjectCreationExpr(nodeFrom, ((ObjectCreationExpr) nodeFrom).resolve().getName());
                break;
            case "EnumDeclaration":
                nodeTo = getEnumDeclaration(nodeFrom, ((EnumDeclaration) nodeFrom).resolve().getName());
                break;
        }

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }

        /* Print and return */
        System.out.println(program);
        return program.clone();
    }

    private static Node getEnumDeclaration(Node nodeFrom, String fromType) {
        // Find enum that matches the nodeTo type
        // Then choose a random entry

        return null;
    }

    private static Node getObjectCreationExpr(Node nodeFrom, String fromType) {
        // Look for object with required nodeTo type
        // Get all constructors of that type and pick one randomly
        // Create new object creation expr with required parameters

        return null;
    }

    private static Node getMethodCallExpr(Node nodeFrom, String fromType) {
        // Look for local method calls before nodeTo line usage

        List<Node> nodes = new ArrayList<>();

        if (nodeFrom.findAncestor(MethodDeclaration.class).isPresent()) {
            // Get all methodCallExpressions before the defined method
            nodeFrom.findAncestor(MethodDeclaration.class).get().findAll(MethodCallExpr.class).forEach(mce -> { if (mce.resolve().getReturnType().describe().equals(fromType) && compareLineNumbers(mce.getBegin(), nodeFrom.getBegin())) { nodes.add(mce); } });
        }

        System.out.println("METHOD CALL NODES " + nodes);
        return nodes.get(GPHelpers.randomIndex(nodes.size()));
    }

    private static Node getVariableDeclarator(Node nodeFrom, String fromType) {
        // Anything in local scope before expression
        // Field declarations
        List<Node> nodes = new ArrayList<>();

        if (nodeFrom.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
            // Get all field variables
            nodeFrom.findAncestor(ClassOrInterfaceDeclaration.class).get().findAll(FieldDeclaration.class).forEach(fd -> fd.getVariables().forEach(vd -> {
                if (vd.resolve().getType().describe().equals(fromType)) {
                    nodes.add(vd.getName().clone());
                }
            }));
        }

        if (nodeFrom.findAncestor(MethodDeclaration.class).isPresent()) {
            MethodDeclaration methodDeclaration = nodeFrom.findAncestor(MethodDeclaration.class).get();

            // Get the parameters of the Method Declaration
            for (int i = 0; i < methodDeclaration.resolve().getNumberOfParams(); i++) {
                if (methodDeclaration.resolve().getParam(i).getType().describe().equals(fromType)) { nodes.add(methodDeclaration.getParameter(i).getName().clone()); }
            }

            // Get locally defined variables before nodeFrom declaration
           methodDeclaration.findAll(VariableDeclarator.class).forEach(vd -> { if (vd.resolve().getType().describe().equals(fromType) && compareLineNumbers(vd.getBegin(), nodeFrom.getBegin())) { nodes.add(vd.getName().clone()); }});
        }

        System.out.println("The parameters of the given method are..." + nodes);

        return nodes.get(GPHelpers.randomIndex(nodes.size()));
    }

    public static List<String> getMethodParams(ResolvedMethodDeclaration method) {
        List <String> params = new ArrayList<>();
        for (int i = 0; i < method.getNumberOfParams(); i++) {
            params.add(method.getParam(i).getType().describe());
        }

        return params;
    }

    public static boolean compareLineNumbers(Optional<Position> position, Optional<Position> nodeDeclarationPosition) {
        if(!position.isPresent() || !nodeDeclarationPosition.isPresent()) { return false; }

        return position.get().line < nodeDeclarationPosition.get().line;
    }

    private static List<Node> collectExpressions(CompilationUnit cu) {
        List<Node> nodes = new ArrayList<>();

        nodes.addAll(cu.findAll(MethodCallExpr.class));
        nodes.addAll(cu.findAll(ObjectCreationExpr.class));
        nodes.addAll(cu.findAll(EnumDeclaration.class));
        cu.findAll(VariableDeclarator.class).forEach(vd -> vd.getInitializer().ifPresent(i -> nodes.addAll(vd.getInitializer().get().findAll(SimpleName.class)))); // get all simple names that are contained in a variable declaration

        System.out.println(nodes);
        return nodes;
    }
}