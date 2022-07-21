package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ReturnStmt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class GNR {

    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // Collect all listed nodes from the compilation unit
        List<Node> nodes = new ArrayList<>(collectExpressions(program));

        // Get random node from subset
        if (nodes.size() == 0) { throw new UnmodifiedProgramException("None of the required nodes could be found in the Compilation Unit"); }
        Node nodeFrom = nodes.get(MutationHelpers.randomIndex(nodes.size()));
        Node nodeTo = null;

        // Determine the node to change to
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

            case "ReturnStmt":
                String type = ((ReturnStmt) nodeFrom).getExpression().isPresent()? ((ReturnStmt) nodeFrom).getExpression().get().calculateResolvedType().describe() : null;

                if (type == null) {
                    if (nodeFrom.findAncestor(MethodDeclaration.class).isPresent())
                        type = nodeFrom.findAncestor(MethodDeclaration.class).get().resolve().getReturnType().describe();
                    else
                        throw new UnmodifiedProgramException("Could not get type of return statement");
                }

                expressions = MutationHelpers.resolveCollection(nodeFrom, type);
                if (expressions.size() > 0) { nodeTo =  ((ReturnStmt) nodeFrom).setExpression(expressions.get(MutationHelpers.randomIndex(expressions.size()))); }
        }

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }

        System.out.println(program);
        return program.clone();
    }

    private static List<Node> collectExpressions(CompilationUnit cu) {
        List<Node> nodes = new ArrayList<>();
        nodes.addAll(cu.findAll(MethodCallExpr.class));
        nodes.addAll(cu.findAll(FieldAccessExpr.class));
        nodes.addAll(cu.findAll(ObjectCreationExpr.class));
        nodes.addAll(cu.findAll(VariableDeclarator.class));
        nodes.addAll(cu.findAll(ReturnStmt.class));

        return nodes;
    }
}