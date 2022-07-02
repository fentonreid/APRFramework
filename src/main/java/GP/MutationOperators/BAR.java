package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import java.util.*;
import java.util.stream.Collectors;

public final class BAR {
    private static final BinaryExpr.Operator[] relationOperators = new BinaryExpr.Operator[]{ BinaryExpr.Operator.LESS, BinaryExpr.Operator.LESS_EQUALS, BinaryExpr.Operator.GREATER, BinaryExpr.Operator.GREATER_EQUALS, BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS };
    private static final BinaryExpr.Operator[] booleanOperators  = new BinaryExpr.Operator[]{ BinaryExpr.Operator.OR, BinaryExpr.Operator.AND};
    private static final UnaryExpr.Operator[] unaryOperators  = new UnaryExpr.Operator[]{ UnaryExpr.Operator.LOGICAL_COMPLEMENT};
    public static CompilationUnit mutate(CompilationUnit program) {
        List<Expression> expressions = new ArrayList<>();

        // -- Collect all :: Unary, Binary and Boolean Literal Expressions in the given Compilation Unit --
        expressions.addAll(program.findAll(UnaryExpr.class).stream().filter(ue -> containsUnaryOperator(ue.getOperator())).collect(Collectors.toList()));
        expressions.addAll(program.findAll(BinaryExpr.class).stream().filter(be -> containsBooleanAndRelationOperator(be.getOperator())).collect(Collectors.toList()));
        expressions.addAll(program.findAll(BooleanLiteralExpr.class));

        // Ensure non-nullness then pick a random expression from the given list
        if (expressions.size() == 0) { throw new NullPointerException("No unary, binary or boolean literal expression found in the given CompilationUnit"); }
        Expression randomExpression = expressions.get(GPHelpers.randomIndex(expressions.size()));

        // Switch on random expressions instance type
        switch(randomExpression.getClass().getSimpleName()) {
            case "UnaryExpr":
                // Remove the given unary expression and get the expression within e.g. !(x > 5) -> (x > 5)
                randomExpression.asUnaryExpr().replace(randomExpression.asUnaryExpr().getExpression());
                break;
            case "BinaryExpr":
                // 50/50 to either introduce a unary expression or switch operator
                if (Math.random() < 0.5) {
                    // Replace boolean/relational operator with random operator of same set
                    BinaryExpr binaryExpr = containsRelationOperator(randomExpression.asBinaryExpr().getOperator()) ? randomExpression.asBinaryExpr().setOperator(relationOperators[GPHelpers.randomIndex(relationOperators.length)]) : randomExpression.asBinaryExpr().setOperator(booleanOperators[GPHelpers.randomIndex(booleanOperators.length)]);
                } else {
                    // Create a new unaryExpression and enclose binary expression in brackets and append a random unary operator
                    randomExpression.replace(new UnaryExpr().setExpression(new EnclosedExpr(randomExpression.clone())).setOperator(unaryOperators[GPHelpers.randomIndex(unaryOperators.length)]));
                }
                break;
            case "BooleanLiteralExpr":
                // Invert the given boolean literal value e.g. true -> false
                randomExpression.asBooleanLiteralExpr().setValue(!randomExpression.asBooleanLiteralExpr().getValue());
                break;
            default:
                throw new TypeNotPresentException("No valid expression was found", null);
        }

        System.out.println("\n\n");
        System.out.println(program);

        return program.clone();
    }

    private static boolean containsUnaryOperator(UnaryExpr.Operator operator) {
        return Arrays.asList(unaryOperators).contains(operator);
    }

    private static boolean containsBooleanOperator(BinaryExpr.Operator operator) {
        return Arrays.asList(booleanOperators).contains(operator);
    }

    private static boolean containsRelationOperator(BinaryExpr.Operator operator) {
        return Arrays.asList(relationOperators).contains(operator);
    }

    private static boolean containsBooleanAndRelationOperator(BinaryExpr.Operator operator) {
        return containsBooleanOperator(operator) || containsRelationOperator(operator) ;
    }
}