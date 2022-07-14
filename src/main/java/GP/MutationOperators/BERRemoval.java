package GP.MutationOperators;

import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BERRemoval {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        List<BinaryExpr> expressions = new ArrayList<>(program.findAll(BinaryExpr.class).stream().filter(BERRemoval::getBinaryExprChildren).collect(Collectors.toList()));
        if (expressions.size() == 0) { throw new Exception("No valid binary expression was found"); }
        
        // Pick base child at random
        BinaryExpr baseChild = expressions.get(MutationHelpers.randomIndex(expressions.size()));
        System.out.println(baseChild);
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
                // Go up the parent tree until a BinaryExpr is found
                while(!(node instanceof BinaryExpr)) {
                    if(!node.getParentNode().isPresent()) { throw new Exception("Could not get binary expression"); }
                    node = node.getParentNode().get();
                }

                // Take children of this node
                siblings = new ArrayList<>(node.getChildNodes());

                // If a sibling is an ancestor of the base child then remove
                siblings.removeIf(child -> child.isAncestorOf(baseChild));

                if (siblings.size() == 0) { throw new Exception("Could not resolve the binary expression '" + node + "'"); }
                siblings.remove(baseChild);
                node.replace(siblings.get(0));
            }

        } else {
            throw new Exception("The boolean expression has only one operator and so cannot be removed");
        }

        System.out.println(program);
        return program.clone();
    }

    public static boolean getBinaryExprChildren(BinaryExpr binaryExpr) {
        // Ensure that the binary expressions children are at a base value e.g. x > 5 or (x > 5)
        return !binaryExpr.getLeft().getClass().getSimpleName().equals("BinaryExpr") && !binaryExpr.getRight().getClass().getSimpleName().equals("BinaryExpr") && !binaryExpr.getLeft().getClass().getSimpleName().equals("EnclosedExpr") && !binaryExpr.getRight().getClass().getSimpleName().equals("EnclosedExpr");
    }
}