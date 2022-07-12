package GP.MutationOperators;

import Util.GPHelpers;
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
        if (nodes.size() < 2) { throw new Exception("Less than two nodes in the given method"); }
        System.out.println(nodes);

        // Take a random node
        Node replacementNode1 = nodes.get(GPHelpers.randomIndex(nodes.size()));

        nodes.remove(replacementNode1);

        // Take another random node
        Node replacementNode2 = nodes.get(GPHelpers.randomIndex(nodes.size()));

        System.out.println(replacementNode1);
        System.out.println(replacementNode2);

        Node temp = replacementNode1.clone();
        System.out.println(replacementNode1.replace(replacementNode2.clone()));
        System.out.println(replacementNode2.replace(temp));

        System.out.println(program);
        return program.clone();
    }

    public static List<Node> nodeCollector(CompilationUnit cu, List<Class<?>> allowedNodeTypes) {
        List<List<Node>> methodNodes = new ArrayList<>();

        // Add all nodes of the AST excluding certain node types we want to skip

        // Kind of want to only remove from one method

        cu.findAll(MethodDeclaration.class).forEach(md -> {
            List<Node> nodeList = new ArrayList<>();
            md.walk(Node.TreeTraversal.PREORDER, node -> {
                if (allowedNodeTypes.contains(node.getClass())) {
                    nodeList.add(node);
                }
            });

            if (nodeList.size() > 1) { methodNodes.add(nodeList); }
        });

        System.out.println("METHOD NODES: " + methodNodes);
        return methodNodes.get(GPHelpers.randomIndex(methodNodes.size()));
    }
}