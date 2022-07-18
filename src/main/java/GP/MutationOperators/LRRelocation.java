package GP.MutationOperators;

import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

public final class LRRelocation {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        List<Class<?>> allowedTypes = new ArrayList<>(LRR.getAllowedNodes());
        allowedTypes.remove(SwitchEntry.class);
        allowedTypes.remove(FieldDeclaration.class);
        allowedTypes.remove(MarkerAnnotationExpr.class);
        allowedTypes.add(SwitchStmt.class);

        List<Node> nodes = nodeCollector(program, allowedTypes);
        if (nodes.size() < 1) { throw new Exception("No acceptable nodes found in this program"); }

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

        System.out.println(program);
        return program.clone();
    }
    
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

        return methodNodes.get(MutationHelpers.randomIndex(methodNodes.size()));
    }
}