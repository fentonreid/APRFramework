package GP.MutationOperators;

import java.util.*;
import Util.MutationHelpers;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.CompilationUnit;

public final class BERExpansion {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        List<BinaryExpr> expressions = new ArrayList<>(program.findAll(BinaryExpr.class));

        // BER Addition
        // Pick a random expression,                                (at any leaf of the binaryExpr nodes)
        // And add a new operator e.g. x > 5 :: x > 5 && y == 2
        if (expressions.size() == 0) { throw new Exception("No valid binary expression was found"); }

        System.out.println("Expressions: " + expressions);

        // Pick a random expression
        BinaryExpr randomExpression = expressions.get(MutationHelpers.randomIndex(expressions.size()));
        System.out.println("PICKED A RANDOM EXPRESSION " + randomExpression);

        // z == 3
        // TO THIS
        // z == 3 && y == 3

        BinaryExpr resultingExpression;
        if (Math.random() < 0.5) {
            resultingExpression = new BinaryExpr().setLeft(randomExpression.clone()).setOperator(MutationHelpers.booleanOperators[MutationHelpers.randomIndex(MutationHelpers.booleanOperators.length)]).setRight(generateExpression(randomExpression));
        } else {
            resultingExpression = new BinaryExpr().setLeft(generateExpression(randomExpression)).setOperator(MutationHelpers.booleanOperators[MutationHelpers.randomIndex(MutationHelpers.booleanOperators.length)]).setRight(randomExpression.clone());
        }

        System.out.println("NEW EXPRESSION " + resultingExpression);
        randomExpression.replace(resultingExpression);

        System.out.println(program);
        return program.clone();
    }

    public static Expression generateExpression(Expression expression) throws Exception {
        double randomState = Math.random();

        // Add a single expression with or without negation
        if (randomState < 0.5) {
            return randomState < 0.25 ? new UnaryExpr().setOperator(UnaryExpr.Operator.LOGICAL_COMPLEMENT).setExpression(getSingleNameExpression(expression)) : getSingleNameExpression(expression);

        // Normal binary expression
        } else {
            return getBinaryExpr(expression);
        }
    }

    private static Expression getSingleNameExpression(Expression randomExpression) {
        List<Expression> booleanTypes = MutationHelpers.resolveCollection(randomExpression, "boolean");

        if (booleanTypes.size() > 0)  return booleanTypes.get(MutationHelpers.randomIndex(booleanTypes.size()));
        throw new NullPointerException("No `Valid Boolean Types` found in the Compilation Unit");
    }

    private static Expression getBinaryExpr(Expression expression) {
        BinaryExpr newExpr =  new BinaryExpr();
        HashMap<String, List<Expression>> expressions = MutationHelpers.resolveAllTypes(expression);

        // Remove types which have less than two nodes available
        expressions.entrySet().stream().filter(entry->entry.getValue().size() < 2).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(expressions.keySet()::remove);
        if (expressions.size() == 0) { throw new NullPointerException("Could not find any allowed nodes in the Compilation Unit"); }

        // Pick at random a hashMap entry
        String randomType = (String) expressions.keySet().toArray()[MutationHelpers.randomIndex(expressions.keySet().size())];

        // Assign newExpr
        BinaryExpr.Operator[] relationalOperators = MutationHelpers.relationOperators;
        System.out.println("TYPES: " + randomType);

        // Deal with Boolean types by restricting the relational operators to: == and !=
        if (randomType.equals("boolean")) {
            relationalOperators = new BinaryExpr.Operator[] {BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS};

        // Deal with String types separately
        } else if (randomType.equals("java.lang.String")) {
            // Use the .equals() method for string comparison, creating a method declaration to do this
            MethodCallExpr mce = new MethodCallExpr();
            mce.setScope(expressions.get(randomType).get(MutationHelpers.randomIndex(expressions.get(randomType).size())));
            mce.setName("equals");
            expressions.get(randomType).remove(mce.getScope());
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