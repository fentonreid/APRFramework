package GP.MutationOperators;

import java.util.*;
import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import java.util.stream.Collectors;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.CompilationUnit;

/**
 * The BER Expansion mutation is a child of the Boolean Expansion and Reduction mutation, with the goal of adding to boolean expressions in the program.<br>
 * Expressions found in if, while and ternary statements are collected with boolean variables and fields and boolean return types collected also.
 */
public final class BERExpansion {

    /**
     * (1) Boolean expressions are collected in the program for example: (x > 5 && y > 5)<br>
     * (2) If no expressions where found then the original program is returned unmodified<br>
     * (3) A random expression from the program is split into child nodes in the example above these would be: x > 5, y > 5<br>
     * (4) A child is picked at random such as the child: x > 5 and a 50/50 decision is made whether to add a new expression to the left or right side of this child<br>
     * (5) Determine what expression to add: single expression with and without negation or a binary expression<br>
     * (6) There is a 25% chance that a single expression of type binary will be added to the child from the local or global scope<br>
     * (7) There is a 25% chance that a single expression of type binary with negation will be added to the child from the local or global scope<br>
     * (8) There is a 50% chance that a binary expression will be added to the program<br>
     *  (8.1) All types in the local/global scope of the child are collected and types with less than two nodes are discarded<br>
     *  (8.2) A random type is chosen if the number of valid types is greater than zero, if not then the program is returned unmodified<br>
     *  (8.3) Two nodes with the same type are set as the left and right nodes of the binary expression<br>
     *  (8.4) The relational operator is randomised, if the type of node is boolean then only == and != are selected and if the type is String then a random string comparison method such as; .equals() or .startsWith() is used for comparison<br>
     * (9) Now that a new expression has been formed a random boolean operator is selected to join the expression and child and the original expression is updated<br>
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        List<Expression> expressions = new ArrayList<>(MutationHelpers.collectStatementExpressions(program));
        if (expressions.size() == 0) { throw new UnmodifiedProgramException("No valid binary expression was found"); }

        // Pick a random expression from the expression list and get the children of that expression
        List<Node> children = MutationHelpers.getChildrenOfExpression(expressions.get(MutationHelpers.randomIndex(expressions.size())));
        Node randomExpression = children.get(MutationHelpers.randomIndex(children.size()));

        // Add at random a new binary expression to either the left or right-hand side of the child expression
        BinaryExpr resultingExpression;
        if (Math.random() < 0.5) {
            resultingExpression = new BinaryExpr().setLeft((Expression) randomExpression.clone()).setOperator(MutationHelpers.booleanOperators[MutationHelpers.randomIndex(MutationHelpers.booleanOperators.length)]).setRight(generateExpression((Expression) randomExpression));
        } else {
            resultingExpression = new BinaryExpr().setLeft(generateExpression((Expression) randomExpression)).setOperator(MutationHelpers.booleanOperators[MutationHelpers.randomIndex(MutationHelpers.booleanOperators.length)]).setRight((Expression) randomExpression.clone());
        }

        randomExpression.replace(resultingExpression);

        return program.clone();
    }

    /**
     * Generate a single expression with a 25%/25% for it to be with or without negation and a 50% chance for it to be a binary expression.
     *
     * @param expression                        The current expression to be modified
     * @return                                  The modified expression either a single expression or binary expression added to it
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static Expression generateExpression(Expression expression) throws UnmodifiedProgramException {
        double randomState = Math.random();

        // Add a single expression with or without negation
        if (randomState < 0.5) {
            return randomState < 0.25 ? new UnaryExpr().setOperator(UnaryExpr.Operator.LOGICAL_COMPLEMENT).setExpression(getSingleNameExpression(expression)) : getSingleNameExpression(expression);

        // Normal binary expression
        } else {
            return getBinaryExpr(expression);
        }
    }

    /**
     * Generates a single name expression using boolean types found in the local and global scope of the given randomExpression.
     *
     * @param expression                        The current expression which will have a single expression added
     * @return                                  The modified expression with a single expression added
     * @throws UnmodifiedProgramException       No valid boolean types could be found in the local or global scope of the expression
     */
    private static Expression getSingleNameExpression(Expression expression) throws UnmodifiedProgramException {
        // Collect boolean and String types in scope
        List<Expression> booleanTypes = MutationHelpers.resolveCollection(expression, "boolean");

        if (booleanTypes.size() == 0) { throw new UnmodifiedProgramException("No `Valid Boolean Types` found in the Compilation Unit"); }

        // Get a random expression
        return booleanTypes.get(MutationHelpers.randomIndex(booleanTypes.size()));
    }

    /**
     * Generates a binary expression using any type found in the AST from the local and global scope of the given randomExpression.
     * If the random type picked from the AST is a boolean then comparison is restricted to != and ==.
     * If the random type is a String then a random comparison method from a list is chosen
     * Otherwise comparison between object exists as usual
     *
     * @param expression                        The current expression which will have a single expression added
     * @return                                  The modified expression with a binary expression added
     * @throws UnmodifiedProgramException       No valid nodes in the compilation unit could be found
     */
    private static Expression getBinaryExpr(Expression expression) throws UnmodifiedProgramException {
        BinaryExpr newExpr =  new BinaryExpr();
        HashMap<String, List<Expression>> expressions = MutationHelpers.resolveAllTypes(expression);

        // Remove types which have less than two nodes available
        expressions.entrySet().stream().filter(entry->entry.getValue().size() < 2).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(expressions.keySet()::remove);
        if (expressions.size() == 0) { throw new UnmodifiedProgramException("Could not find any allowed nodes in the Compilation Unit"); }

        // Pick at random a hashMap entry
        String randomType = (String) expressions.keySet().toArray()[MutationHelpers.randomIndex(expressions.keySet().size())];

        // Assign newExpr
        BinaryExpr.Operator[] relationalOperators = MutationHelpers.relationOperators;

        // Deal with Boolean types by restricting the relational operators to: == and !=
        if (randomType.equals("boolean")) {
            relationalOperators = new BinaryExpr.Operator[] {BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS};

            newExpr.setLeft(expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size())));
            newExpr.setOperator(relationalOperators[MutationHelpers.randomIndex(relationalOperators.length)]);
            expressions.get(randomType).remove(newExpr.getLeft());

            newExpr.setRight(expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size())));

            return newExpr;

            // Deal with String types separately
        } else if (randomType.equals("java.lang.String")) {
            // A string comparison method is chosen at random
            MethodCallExpr mce = new MethodCallExpr();
            Expression randomExpression = expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size()));
            mce.setScope(randomExpression);
            mce.setName(MutationHelpers.binaryExpressionAllowedMethods[MutationHelpers.randomIndex(MutationHelpers.binaryExpressionAllowedMethods.length)]);
            expressions.get(randomType).remove(randomExpression);
            mce.setArguments(new NodeList<>(expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size()))));

            return mce;
        }

        newExpr.setLeft(expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size())));
        expressions.get(randomType).remove(newExpr.getLeft());

        // 10 chance to add a null comparison to the binary expression
        if (Math.random() < 0.9) {
            newExpr.setRight(expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size())));
            newExpr.setOperator(relationalOperators[MutationHelpers.randomIndex(relationalOperators.length)]);
        } else {
            relationalOperators = new BinaryExpr.Operator[] {BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS};
            newExpr.setOperator(relationalOperators[MutationHelpers.randomIndex(relationalOperators.length)]);
            newExpr.setRight(new NameExpr().setName("null"));
        }

        return newExpr;
    }
}