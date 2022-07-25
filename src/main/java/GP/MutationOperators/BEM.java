package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import java.util.*;

/**
 * The Boolean Expression Modification (BEM) mutation is a child of the Boolean And Relational (BAR) mutation, with the goal of switching the boolean and relational operators of expressions<br>
 * and adding and removing negation to child expressions.<br><br>
 * Expressions found in if, while and ternary statements are collected with boolean variables and fields and boolean return types collected also.
 */
public final class BEM {

    /**
     * (1) Boolean expressions in the program are collected, for example (x > 5 && y > 5)<br>
     * (2) If a valid expression could not be found then return the program unmodified<br>
     * (3) A random expression from the list of available expressions is chosen and different actions are taken depending on the instance type<br>
     * (4) If the expression is a Unary expression then remove the unary expression and get the expression inside<br>
     * (5) If the expression is a Binary expression then the mutation has two options<br>
     *  (5.1) 50% chance to modify the binary expressions operator<br>
     *   (5.1.1) Generally, if the current operator of the expression is a boolean operator (&&, ||) then a random boolean operator is chosen<br>
     *   (5.1.2) Generally, if the current operator of the expression is a relational operator (!=, <=, ...) then a random relational operator is chosen<br>
     *   (5.1.3) If the binary expression is a string then .equals() is used for the relational operator and if a boolean then the relational operators are limited to == and !=<br>
     *  (5.2) 50% chance to enclose the expression in a negation operator<br>
     * (6) If the expression is a BooleanLiteralExpr e.g. a 'true' or 'false' value then the boolean literal is flipped<br>
     * (7) The modified program is returned
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
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

        return program.clone();
    }

    /**
     * Returns if the given operator appears in the relational operators array. Useful to determine if a binary expression is a child node or not.
     *
     * @param operator  The given operator of an expression
     * @return          If the operator is present in the relational operators array or not
     */
    private static boolean containsRelationOperator(BinaryExpr.Operator operator) {
        return Arrays.asList(MutationHelpers.relationOperators).contains(operator);
    }
}