package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The LRRelocation mutation is a child of the Line Relocation and Removal (LRR) mutation, with the goal of moving statements in the same method around.<br>
 * Variable declarations, method calls, if, while, do-while, for, try and switch statements, and throw statements can all be moved.
 */
public final class LRRelocation {

    /**
     * (1) Collect from the program all nodes that are specified in the LRR 'getAllowedNodes()' method, for this mutation this list is modified by the mutation to remove Switch entries, Field declarations and a few others<br>
     * (2) Two unique nodes are selected from the program that comply to the allowed nodes, if two unique nodes cannot be found then the program is returned unmodified<br>
     * (3) The second node with the name of 'nodeTo' which is the location nodeFrom will relocate to, does not have a block statement ancestor then the program is returned as the mutation requires a block statement for relocation<br>
     * (4) NodeFrom is copied above nodeTo using the 'addBefore()' method that BlockStmt provides, and the original nodeFrom is removed<br>
     * (5) The modified program is then returned
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        List<Class<?>> allowedTypes = new ArrayList<>(LRR.getAllowedNodes());
        allowedTypes.remove(SwitchEntry.class);
        allowedTypes.remove(FieldDeclaration.class);
        allowedTypes.remove(MarkerAnnotationExpr.class);
        allowedTypes.remove(ReturnStmt.class);
        allowedTypes.remove(ContinueStmt.class);
        allowedTypes.remove(BreakStmt.class);
        allowedTypes.add(SwitchStmt.class);

        List<Node> nodes = nodeCollector(program, allowedTypes);
        if (nodes == null || nodes.size() < 2) { throw new UnmodifiedProgramException("Less than two acceptable nodes found in the program"); }

        // Take a random node
        Node nodeFrom = nodes.get(MutationHelpers.randomIndex(nodes.size()));
        nodes.remove(nodeFrom);

        // Take another node in the program
        Node nodeTo = nodes.get(MutationHelpers.randomIndex(nodes.size()));

        // If the parent of the node to insert before is not part of a block statement then the original program is returned as a block statement is needed
        if (!nodeTo.findAncestor(BlockStmt.class).isPresent()) { return program.clone(); }

        BlockStmt nodeToBlock = nodeTo.findAncestor(BlockStmt.class).get();
        nodeToBlock.getStatements().addBefore((Statement) nodeFrom.clone(), (Statement) nodeTo.clone());
        nodeFrom.removeForced();

        return program.clone();
    }

    /**
     * Collects all allowed nodes from the program as defined in the LRR getAllowedNodes() method that have at least two types.
     *
     * @param cu                    The AST representation of the current program
     * @param allowedNodeTypes      The name of the type that is to be collected from the program
     * @return                      A list of nodes from the program that are allowed from the LRR allowedNodes definition
     */
    public static List<Node> nodeCollector(CompilationUnit cu, List<Class<?>> allowedNodeTypes) {
        List<List<Node>> methodNodes = new ArrayList<>();

        // Add all nodes of the AST excluding certain node types we want to skip
        cu.findAll(MethodDeclaration.class).forEach(md -> {
            List<Node> nodeList = new ArrayList<>();
            md.walk(Node.TreeTraversal.PREORDER, node -> {
                if (allowedNodeTypes.contains(node.getClass())) {
                    nodeList.add(node);
                }
            });

            if (nodeList.size() > 1) { methodNodes.add(nodeList); }
        });
        
        if (methodNodes.size() > 1) { return methodNodes.get(MutationHelpers.randomIndex(methodNodes.size())); }

        return null;
    }
}