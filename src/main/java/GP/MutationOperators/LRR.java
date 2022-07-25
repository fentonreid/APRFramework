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

/**
 * The Line Removal and Relocation (LRR) mutation combines the functionality of both the LRRelocation and LRRemoval mutations, with the mutation being able to;<br>
 * move statements in the same method around, and remove specific nodes in the program as a whole around too.
 */
public final class LRR {

    /**
     * (1) Determine which mutation to choose from, 50/50 for either LRRelocation or LRRemoval<br>
     * (2) Run the mutation and return the modified program back
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // Remove
        if (Math.random() < 0.5) { return LRRemoval.mutate(program); }

        // Relocate
        return LRRelocation.mutate(program);
    }

    /**
     * A list of allowed nodes that can be collected from the AST for the LRR mutations children.
     *
     * @return  A List of allowed classes that can be collected by the mutation
     */
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

        return allowedNodeTypes;
    }

    /**
     * Collects all allowed nodes from the program as defined in the LRR getAllowedNodes().
     *
     * @param cu                    The AST representation of the current program
     * @param allowedTypes      The name of the type that is to be collected from the program
     * @return                      A list of nodes from the program that are allowed from the LRR allowedNodes definition
     */
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