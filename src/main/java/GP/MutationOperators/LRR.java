package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

public final class LRR {
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // Remove
        if (Math.random() < 0.5) { return LRRemoval.mutate(program); }

        // Relocate
        return LRRelocation.mutate(program);
    }

    public static List<Class<?>> getAllowedNodes() {
        List<Class<?>> allowedNodeTypes = new ArrayList<>();
        allowedNodeTypes.add(AssignExpr.class);
        allowedNodeTypes.add(BreakStmt.class);
        allowedNodeTypes.add(TryStmt.class);
        allowedNodeTypes.add(ContinueStmt.class);
        allowedNodeTypes.add(DoStmt.class);
        allowedNodeTypes.add(FieldDeclaration.class);
        allowedNodeTypes.add(ForStmt.class);
        allowedNodeTypes.add(ForEachStmt.class);
        allowedNodeTypes.add(IfStmt.class);
        allowedNodeTypes.add(LambdaExpr.class);
        allowedNodeTypes.add(MarkerAnnotationExpr.class);
        allowedNodeTypes.add(ExpressionStmt.class);
        allowedNodeTypes.add(ReturnStmt.class);
        allowedNodeTypes.add(SwitchEntry.class);
        allowedNodeTypes.add(ThrowStmt.class);
        allowedNodeTypes.add(WhileStmt.class);
        allowedNodeTypes.add(MethodDeclaration.class);

        return allowedNodeTypes;
    }

    public static List<Node> nodeCollector(CompilationUnit cu, List<Class<?>> allowedTypes) {
        List<Class<?>> allowedNodeTypes = new ArrayList<>(allowedTypes);

        // Add all nodes of the AST excluding certain node types we want to skip
        List<Node> nodeList = new ArrayList<>();
        cu.walk(Node.TreeTraversal.PREORDER, node -> {
            if (allowedNodeTypes.contains(node.getClass())) {
                nodeList.add(node);
            }
        });

        return nodeList;
    }
}