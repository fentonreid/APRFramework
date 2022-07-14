package GP.MutationOperators;

import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

public final class LRRemoval {
    public static CompilationUnit mutate(CompilationUnit program) {
        // Remove a statement or expression from the program
        List<Node> nodeList = LRR.nodeCollector(program, LRR.getAllowedNodes());
        Node removeNode = nodeList.get(MutationHelpers.randomIndex(nodeList.size()));

        switch(removeNode.getClass().getSimpleName()) {
            case "TryStmt":
                TryStmt tryStmt = (TryStmt) removeNode;

                // Collect the try statement, catch statements and finally statements
                List<Node> tryNodes = new ArrayList<>();
                tryNodes.add(tryStmt.getTryBlock());
                tryNodes.addAll(tryStmt.getCatchClauses());
                tryStmt.getFinallyBlock().ifPresent(tryNodes::add);

                Node removeTryNode = tryNodes.get(MutationHelpers.randomIndex(tryNodes.size()));
                removeTryNode.removeForced();
            default:
                removeNode.removeForced();
        }

        return program.clone();
    }
}