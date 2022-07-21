package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BERReduction {
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        List<Expression> expressions = new ArrayList<>(collectBooleanExpressions(program));
        if (expressions.size() == 0) { throw new UnmodifiedProgramException("No valid binary expression was found"); }

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

    private static List<Expression> collectBooleanExpressions(CompilationUnit cu) {
        List<Expression> expressions = new ArrayList<>();
        Set<Integer> expressionLineDefinition = new HashSet<>();

        cu.findAll(BinaryExpr.class).forEach(binaryExpr -> {
                    if (binaryExpr.getBegin().isPresent() && !expressionLineDefinition.contains(binaryExpr.getBegin().get().line)) {
                        expressionLineDefinition.add(binaryExpr.getBegin().get().line);
                        expressions.add(binaryExpr);
                    }
                }
        );

        return expressions;
    }
}