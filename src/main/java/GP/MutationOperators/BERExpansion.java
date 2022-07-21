package GP.MutationOperators;

import java.util.*;
import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import java.util.stream.Collectors;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.CompilationUnit;

public final class BERExpansion {
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

        System.out.println(program);
        return program.clone();
    }

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

    private static Expression getSingleNameExpression(Expression randomExpression) throws UnmodifiedProgramException {
        // Collect boolean and String types in scope
        List<Expression> booleanTypes = MutationHelpers.resolveCollection(randomExpression, "boolean");

        if (booleanTypes.size() == 0) { throw new UnmodifiedProgramException("No `Valid Boolean Types` found in the Compilation Unit"); }

        // Get a random expression
        return booleanTypes.get(MutationHelpers.randomIndex(booleanTypes.size()));
    }

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
        newExpr.setOperator(relationalOperators[MutationHelpers.randomIndex(relationalOperators.length)]);
        expressions.get(randomType).remove(newExpr.getLeft());
        newExpr.setRight(expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size())));

        return newExpr;
    }
}