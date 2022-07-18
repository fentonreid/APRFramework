package GP.MutationOperators;

import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.printer.YamlPrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BERRemoval {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        YamlPrinter test = new YamlPrinter(true);
        System.out.println(test.output(program));

        List<Expression> expressions = new ArrayList<>(collectBooleanExpressions(program));
        if (expressions.size() == 0) { throw new Exception("No valid binary expression was found"); }

        System.out.println("Expressions: " + expressions);

        List<Node> children = new ArrayList<>(MutationHelpers.getChildrenOfExpression(expressions.get(MutationHelpers.randomIndex(expressions.size()))));
        System.out.println("CHILDREN: " + children);

        if (children.size() == 0) { throw new Exception("No valid children found"); }


        // Pick base child at random
        Node baseChild = children.get(MutationHelpers.randomIndex(children.size()));
        System.out.println("BASE CHILD: " + baseChild);

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
                if(!node.findAncestor(BinaryExpr.class).isPresent()) { throw new Exception("Could not get binary expression"); }
                node = node.findAncestor(BinaryExpr.class).get();

                // Get children of the Binary expression and if a sibling is an ancestor of the base child then remove
                siblings = new ArrayList<>(node.getChildNodes());
                siblings.removeIf(child -> child.isAncestorOf(baseChild));

                if (siblings.size() == 0) { throw new Exception("Could not resolve the binary expression '" + node + "'"); }
                siblings.remove(baseChild);
                node.replace(siblings.get(0));
            }

        } else {
            throw new NullPointerException("The boolean expression has only one operator and so cannot be removed");
        }

        System.out.println(program);
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