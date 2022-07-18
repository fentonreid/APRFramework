package GP.MutationOperators;

import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.printer.YamlPrinter;
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
                break;

            case "IfStmt":
                IfStmt ifStmt = (IfStmt) removeNode;

                // If an else statement exists then there is a 50% chance the else statement will be removed
                if (ifStmt.hasElseBranch() && Math.random() < 0.5) {
                    ifStmt.removeElseStmt();
                } else {
                    removeNode.removeForced();
                }
                break;

            default:
                removeNode.removeForced();
        }

        System.out.println(program);
        return program.clone();
    }

    private static Statement getElseAndElseIfStmt(IfStmt ifStmt) {
        List<Statement> branches = new ArrayList<>();

        // Get all else if branches
        for (Node stmt : ifStmt.getChildNodes()) {
            if (stmt instanceof IfStmt && ((IfStmt) stmt).getElseStmt().isPresent()) {
                branches.add(((IfStmt) stmt).getElseStmt().get());
            }
        }

        // Have to figure out how to do this


        return null;


    }
}