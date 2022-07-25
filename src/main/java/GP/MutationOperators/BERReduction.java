package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;

import java.util.*;

/**
 * The BER Reduction mutation is a child of the Boolean Expansion and Reduction mutation, with the goal of reducing compound boolean expressions in the program.<br>
 * Binary expressions in: if, while and for loops can be collected with boolean variables and fields and boolean return types collected also.
 */
public final class BERReduction {

    /**
     * (1) Binary expression parents are collected in the program, for example (x > 5 && y > 5 && z > 5) is a binary expr parent, where (y > 5 && z > 5) is a binary expression child and is not collected<br>
     * (2) If a valid expression could not be found then return the program unmodified<br>
     * (3) The parent binary expression is split into child nodes in the example above these would be: x > 5, y > 5 and z > 5<br>
     * (4) A child is picked at random such as the child: x > 5<br>
     * (5) If the parent to the child is a binary expression then the siblings of the child are collected through the parent and the child node is removed<br>
     * (6) If the child is wrapped in a unary, ! or enclosed expression, () then the binary expression of the child is found and the same process as step five is applied
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        List<Expression> expressions = new ArrayList<>(collectBinaryExpressions(program));
        if (expressions.size() == 0) { throw new UnmodifiedProgramException("No valid binary expression was found"); }

        System.out.println(expressions);

        List<Node> children = new ArrayList<>(MutationHelpers.getChildrenOfExpression(expressions.get(MutationHelpers.randomIndex(expressions.size()))));
        if (children.size() == 0) { throw new UnmodifiedProgramException("No valid children found"); }

        // Pick base child at random
        Node baseChild = children.get(MutationHelpers.randomIndex(children.size()));

        // Get the parent of the base child and ensure that it is a compound boolean expression
        if (baseChild.getParentNode().isPresent() && (baseChild.getParentNode().get() instanceof Expression)) {
            Node node = baseChild.getParentNode().get();

            // Get the siblings which includes the base node and remove it
            List<Node> siblings = new ArrayList<>(node.getChildNodes());
            siblings.remove(baseChild);

            // Replace the parent node with child
            if(siblings.size() > 0) {
                node.replace(siblings.get(0));

                // Wrapped in a unary or enclosed expression
            } else {
                // Find the Binary expression attached to this node
                if(!node.findAncestor(BinaryExpr.class).isPresent()) { throw new UnmodifiedProgramException("Could not get binary expression"); }
                node = node.findAncestor(BinaryExpr.class).get();

                // Get children of the Binary expression and if a sibling is an ancestor of the base child then remove
                siblings = new ArrayList<>(node.getChildNodes());
                siblings.removeIf(child -> child.isAncestorOf(baseChild));

                if (siblings.size() == 0) { throw new UnmodifiedProgramException("Could not resolve the binary expression '" + node + "'"); }
                siblings.remove(baseChild);
                node.replace(siblings.get(0));
            }

        } else {
            throw new UnmodifiedProgramException("The boolean expression has only one operator and so cannot be removed");
        }

        return program.clone();
    }

    /**
     * Collects all binary expression parents from the Compilation Unit that exist before the node of interest.<br>
     * The line number of each processed binary expression is kept to prevent child nodes of the same expression from being collected.<br>
     * The operator of the binary expression is also checked to allow only binary expressions conjoined by || or && to be collected.
     *
     * @param cu    The AST representation of the current program
     * @return      A List of binary expressions that have only the parent definition
     */
    private static List<Expression> collectBinaryExpressions(CompilationUnit cu) {
        List<Expression> expressions = new ArrayList<>();
        Set<Integer> expressionLineDefinition = new HashSet<>();

        cu.findAll(BinaryExpr.class).forEach(binaryExpr -> {
                    // Only get the parent binary expression, and ensure that the operator that separates them is either || or &&
                    if (binaryExpr.getBegin().isPresent() && !expressionLineDefinition.contains(binaryExpr.getBegin().get().line) && Arrays.asList(MutationHelpers.booleanOperators).contains(binaryExpr.getOperator())) {
                        expressionLineDefinition.add(binaryExpr.getBegin().get().line);
                        expressions.add(binaryExpr);
                    }
                }
        );

        return expressions;
    }
}