package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.resolution.types.ResolvedType;

import java.util.ArrayList;
import java.util.List;

public final class SVM {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        /* (1) -- Need to collect */
        // - Variable Declarations
        // - Method calls
        // - Object creation expressions
        // - Enums
        List<Node> nodes = new ArrayList<>(collectExpressions(program));
        
        /* (2) -- Decide what to switch to and from */
        // a - Variable
        // b - Method Call Expression
        // c - Object Creation Expression
        // d - Enum
        String[] stateList = new String[]{"VariableDeclarator", "MethodCallExpr", "ObjectCreationExpr", "EnumDeclaration"};
        Node nodeTo = nodes.get(GPHelpers.randomIndex(nodes.size()));
        String to = nodeTo.getClass().getSimpleName();
        String from = stateList[GPHelpers.randomIndex(stateList.length)];
        System.out.println(to + " -> " + from);

        /* (3) -- Finding a switch */
        Node nodeFrom = null;
        switch (to) {
            case "VariableDeclarator":
                nodeFrom = getVariableDeclarator(nodeTo);
                break;
            case "MethodCallExpr":
                nodeFrom = getMethodCallExpr(nodeTo);
                break;
            case "ObjectCreationExpr":
                nodeFrom = getObjectCreationExpr(nodeTo);
                break;
            case "EnumDeclaration":
                nodeFrom = getEnumDeclaration(nodeTo);
                break;
        }

        /* Perform replacement */
        nodeTo.replace(nodeFrom);

        /* Print and return */
        System.out.println(program);
        return program.clone();
    }

    private static Node getEnumDeclaration(Node nodeTo) {
        // Find enum that matches the nodeTo type
        // Then choose a random entry

        return null;
    }

    private static Node getObjectCreationExpr(Node nodeTo) {
        // Look for object with required nodeTo type
        // Get all constructors of that type and pick one randomly
        // Create new object creation expr with required parameters

        return null;
    }

    private static Node getMethodCallExpr(Node nodeTo) {
        // Look into all classes and get all methods
        // Look for methods with return type of nodeTo type
        // Look for local method calls before nodeTo line usage
        // Use type solver for basic types** MAYBE

        return null;
    }

    private static Node getVariableDeclarator(Node nodeTo) {
        // Anything in local scope before expression
        // Field declarations
        // Method parameters of local scope

        return null;
    }

    private static List<Node> collectExpressions(CompilationUnit cu) {
        List<Node> nodes = new ArrayList<>();

        nodes.addAll(cu.findAll(VariableDeclarator.class));
        nodes.addAll(cu.findAll(MethodCallExpr.class));
        nodes.addAll(cu.findAll(ObjectCreationExpr.class));
        nodes.addAll(cu.findAll(EnumDeclaration.class));

        System.out.println(nodes);

        return nodes;
    }
}