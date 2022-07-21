package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import java.util.*;

public final class BEM {
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        List<Expression> expressions = new ArrayList<>(MutationHelpers.collectStatementExpressions(program));

        // Ensure non-nullness then pick a random expression from the given list
        if (expressions.size() == 0) { throw new UnmodifiedProgramException("No expression found in the given CompilationUnit"); }
        Expression randomExpression = expressions.get(MutationHelpers.randomIndex(expressions.size()));

        // Switch on random expressions instance type
        switch(randomExpression.getClass().getSimpleName()) {
            case "UnaryExpr":
                // Remove the given unary expression and get the expression within e.g. !(x > 5) -> (x > 5)
                randomExpression.asUnaryExpr().replace(randomExpression.asUnaryExpr().getExpression());
                break;

            case "BinaryExpr":
                // 50/50 to either introduce a unary expression or switch boolean/relational operator
                if (Math.random() < 0.5) {
                    // If the randomExpression left side is a boolean then comparison needs to be between
                    if (randomExpression.asBinaryExpr().getLeft().calculateResolvedType().describe().equals("boolean")) {
                        BinaryExpr.Operator[] binaryOperators = new BinaryExpr.Operator[] { BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS };
                        BinaryExpr binaryExpr = containsRelationOperator(randomExpression.asBinaryExpr().getOperator()) ? randomExpression.asBinaryExpr().setOperator(binaryOperators[MutationHelpers.randomIndex(binaryOperators.length)]) : randomExpression.asBinaryExpr().setOperator(MutationHelpers.booleanOperators[MutationHelpers.randomIndex(MutationHelpers.booleanOperators.length)]);

                    } else if (randomExpression.asBinaryExpr().getLeft().calculateResolvedType().describe().equals("java.lang.String")) {
                        if (containsRelationOperator(randomExpression.asBinaryExpr().getOperator())) {
                            MethodCallExpr mce = new MethodCallExpr();
                            mce.setName(MutationHelpers.binaryExpressionAllowedMethods[MutationHelpers.randomIndex(MutationHelpers.binaryExpressionAllowedMethods.length)]);
                            NodeList<Expression> arguments = new NodeList<>();

                            // Choose either left or right-side as parameter of random String comparison method
                            if (Math.random() < 0.5) {
                                mce.setScope(randomExpression.asBinaryExpr().getLeft());
                                arguments.add(randomExpression.asBinaryExpr().getRight());
                                mce.setArguments(arguments);

                            } else {
                                mce.setScope(randomExpression.asBinaryExpr().getRight());
                                arguments.add(randomExpression.asBinaryExpr().getLeft());
                                mce.setArguments(arguments);
                            }

                            randomExpression.replace(mce);

                        } else {
                            randomExpression.asBinaryExpr().setOperator(MutationHelpers.booleanOperators[MutationHelpers.randomIndex(MutationHelpers.booleanOperators.length)]);
                        }

                    } else {
                        BinaryExpr binaryExpr = containsRelationOperator(randomExpression.asBinaryExpr().getOperator()) ? randomExpression.asBinaryExpr().setOperator(MutationHelpers.relationOperators[MutationHelpers.randomIndex(MutationHelpers.relationOperators.length)]) : randomExpression.asBinaryExpr().setOperator(MutationHelpers.booleanOperators[MutationHelpers.randomIndex(MutationHelpers.booleanOperators.length)]);
                    }

                } else {
                    // Create a new unaryExpression and enclose binary expression in brackets and append a random unary operator
                    randomExpression.replace(new UnaryExpr().setExpression(new EnclosedExpr(randomExpression.clone())).setOperator(MutationHelpers.unaryOperators[MutationHelpers.randomIndex(MutationHelpers.unaryOperators.length)]));
                }
                break;

            case "BooleanLiteralExpr":
                // Invert the given boolean literal value e.g. true -> false
                randomExpression.asBooleanLiteralExpr().setValue(!randomExpression.asBooleanLiteralExpr().getValue());
                break;

            default:
                // Any other expression -- take the expression and add negation to it
                randomExpression.replace(new UnaryExpr().setOperator(UnaryExpr.Operator.LOGICAL_COMPLEMENT).setExpression(randomExpression.clone()));
        }

        System.out.println(program);
        return program.clone();
    }

    private static boolean containsRelationOperator(BinaryExpr.Operator operator) {
        return Arrays.asList(MutationHelpers.relationOperators).contains(operator);
    }
}