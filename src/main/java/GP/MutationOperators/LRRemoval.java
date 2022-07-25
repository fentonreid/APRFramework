package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The LRRemoval mutation is a child of the Line Removal and Relocation (LRR) mutation, with the goal of removing statements.<br>
 * These include, if, while, do-while, for, try and switch statements, methods calls, variable declarations, throw, break, continue and return keywords. Whole method declarations can be removed also.
 */
public final class LRRemoval {

    /**
     * (1) Collect from the program all nodes that are specified in the LRR 'getAllowedNodes()' method<br>
     * (2) If a valid node could not be found then return the program unmodified<br>
     * (3) Pick a random node from the list of available nodes. Special rules exist for the Try and IF statement<br>
     *  (3.1) If the random node is a Try statement, then the try statement, catch and/or finally statements are collected<br>
     *   (3.1.1) A catch block is only collected if there exists more than one catch or a finally statement is present<br>
     *   (3.1.2) A random statement is removed from the collected list with there always being atleast a try statement present<br>
     *  (3.2) If the random node is an IF statement, if an ELSE statement is present then the else statement is removed otherwise, the IF statement is removed<br>
     * (4) All other nodes are removed without issue and the modified program is returned
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // Remove a statement or expression from the program
        List<Node> nodeList = LRR.nodeCollector(program, LRR.getAllowedNodes());
        if (nodeList.size() == 0) { throw new UnmodifiedProgramException("No available nodes found to perform a LRRemoval mutation"); }

        Node removeNode = nodeList.get(MutationHelpers.randomIndex(nodeList.size()));

        switch(removeNode.getClass().getSimpleName()) {
            case "TryStmt":
                TryStmt tryStmt = (TryStmt) removeNode;

                // Collect the try statement, catch statements and finally statements
                List<Node> tryNodes = new ArrayList<>();
                tryNodes.add(tryStmt.getTryBlock());

                // If the program has more than one catch block or has a finally block then we can remove a catch block
                if (tryStmt.getCatchClauses().size() > 1 || tryStmt.getFinallyBlock().isPresent()) {
                    tryNodes.addAll(tryStmt.getCatchClauses());
                    tryStmt.getFinallyBlock().ifPresent(tryNodes::add);
                }

                Node removeTryNode = tryNodes.get(MutationHelpers.randomIndex(tryNodes.size()));
                removeTryNode.removeForced();
                break;

            case "IfStmt":
                IfStmt ifStmt = (IfStmt) removeNode;

                // If an else statement exists then the else statement will be removed
                if (ifStmt.hasElseBranch()) {
                    ifStmt.removeElseStmt();
                } else {
                    removeNode.removeForced();
                }
                break;

            default:
                removeNode.removeForced();
        }

        return program.clone();
    }
}