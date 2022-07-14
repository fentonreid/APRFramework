package GP.MutationOperators;

import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import java.util.ArrayList;
import java.util.List;

public final class SVM {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        /* (1) -- Collect all listed nodes from the compilation unit */
        // - Variable Declarations
        // - Method Calls
        // - Object Instantiations
        // - Field Access Call
        List<Node> nodes = new ArrayList<>(collectExpressions(program));

        /* (2) -- Get random node from subset */
        if (nodes.size() == 0) { throw new Exception("None of the required nodes could be found in the Compilation Unit"); }
        Node nodeFrom = nodes.get(MutationHelpers.randomIndex(nodes.size()));
        System.out.println("Single node: " + nodeFrom);
        Node nodeTo = null;

        /* (3) -- Determine the node to change to */
        System.out.println(nodeFrom.getClass().getSimpleName());
        List<Expression> expressions;
        switch (nodeFrom.getClass().getSimpleName()) {
            case "VariableDeclarator":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((VariableDeclarator) nodeFrom).resolve().getType().describe());
                if (expressions.size() > 0) { nodeTo = ((VariableDeclarator) nodeFrom).clone().setInitializer(expressions.get(MutationHelpers.randomIndex(expressions.size()))); }
                break;

            case "MethodCallExpr":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((MethodCallExpr) nodeFrom).resolve().getReturnType().describe());
                if (expressions.size() > 0) { nodeTo = expressions.get(MutationHelpers.randomIndex(expressions.size())); }
                break;

            case "ObjectCreationExpr":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((ObjectCreationExpr) nodeFrom).resolve().getClassName());
                if (expressions.size() > 0) { nodeTo = expressions.get(MutationHelpers.randomIndex(expressions.size())); }
                break;

            case "FieldAccessExpr":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((FieldAccessExpr) nodeFrom).resolve().getType().describe());
                if (expressions.size() > 0) { nodeTo = expressions.get(MutationHelpers.randomIndex(expressions.size())); }
                break;
        }

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }

        /* Print and return */
        System.out.println(program);
        return program.clone();
    }

    private static List<Node> collectExpressions(CompilationUnit cu) {
        List<Node> nodes = new ArrayList<>();
        nodes.addAll(cu.findAll(MethodCallExpr.class));
        nodes.addAll(cu.findAll(FieldAccessExpr.class));
        nodes.addAll(cu.findAll(ObjectCreationExpr.class));
        nodes.addAll(cu.findAll(VariableDeclarator.class));

        System.out.println("NODES COLLECTED: " + nodes);
        return nodes;
    }
}