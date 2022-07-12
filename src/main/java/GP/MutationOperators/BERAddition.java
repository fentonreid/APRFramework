// WANT TO GET IT TO WORK WHERE I CAN CHECK THE COMPILATION UNIT FOR METHODS
package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.printer.YamlPrinter;

import java.util.*;
import java.util.stream.Collector;

import static GP.MutationOperators.SVM.compareLineNumbers;

public final class BERAddition {
    private static final BinaryExpr.Operator[] relationOperators = new BinaryExpr.Operator[]{ BinaryExpr.Operator.LESS, BinaryExpr.Operator.LESS_EQUALS, BinaryExpr.Operator.GREATER, BinaryExpr.Operator.GREATER_EQUALS, BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS };
    private static final BinaryExpr.Operator[] booleanOperators  = new BinaryExpr.Operator[]{ BinaryExpr.Operator.OR, BinaryExpr.Operator.AND};
    private static final UnaryExpr.Operator[] unaryOperators  = new UnaryExpr.Operator[]{ UnaryExpr.Operator.LOGICAL_COMPLEMENT};
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        YamlPrinter printer = new YamlPrinter(true);
        //System.out.println(printer.output(program));

        List<BinaryExpr> expressions = new ArrayList<>(program.findAll(BinaryExpr.class));

        // BER Addition
        // Pick a random expression,                                (at any leaf of the binaryExpr nodes)
        // And add a new operator e.g. x > 5 :: x > 5 && y == 2
        if (expressions.size() == 0) { throw new Exception("No valid binary expression was found"); }

        System.out.println("Expressions: " + expressions);

        // Pick a random expression
        BinaryExpr randomExpression = expressions.get(GPHelpers.randomIndex(expressions.size()));
        System.out.println("PICKED A RANDOM EXPRESSION " + randomExpression);

        // z == 3
        // TO THIS
        // z == 3 && y == 3

        BinaryExpr resultingExpression = null;
        if (Math.random() < 0.5)
            resultingExpression = new BinaryExpr().setLeft(randomExpression.clone()).setOperator(booleanOperators[GPHelpers.randomIndex(booleanOperators.length)]).setRight(generateExpression(program, randomExpression));
        else
            resultingExpression = new BinaryExpr().setLeft(generateExpression(program, randomExpression)).setOperator(booleanOperators[GPHelpers.randomIndex(booleanOperators.length)]).setRight(randomExpression.clone());

        System.out.println("NEW EXPRESSION " + resultingExpression);


        randomExpression.replace(resultingExpression);

        System.out.println(program);
        return program.clone();
    }

    public static Expression generateExpression(CompilationUnit cu, Expression expression) {
        double randomState = Math.random();

        // Add a not value
        if (randomState < 0.01) { // 0.25
            UnaryExpr ue = new UnaryExpr();
            ue.setOperator(UnaryExpr.Operator.LOGICAL_COMPLEMENT);
            ue.setExpression(getSingleNameExpression(cu, expression)); // must resolve to a boolean type
            return ue;

        // Add a boolean value e.g. hasStarted
        } else if (randomState < 0.01) { // 0.5
            NameExpr nameExpr = (NameExpr) getSingleNameExpression(cu, expression); // must be resolve to a boolean type
            return nameExpr;

        // Normal binary expression
        } else {
            System.out.println("BINARY EXPRESSION :PPPP");
            return getBinaryExpr(expression);
        }
    }

    private static Expression getBinaryExpr(Expression expression) {
        BinaryExpr newExpr =  new BinaryExpr();

        // Get a list of types that are possible,
        // pick one
        // get left and get right
        HashMap<String, List<Expression>> expressions = getExpressionsInSpecificClassAndMethodWithoutType(expression);

        // Remove types which have less than two nodes available
        for (Map.Entry<String, List<Expression>> expr : expressions.entrySet()) { if (expr.getValue().size() < 2) { expressions.remove(expr.getKey()); } }
        if (expressions.size() == 0) { throw new NullPointerException("Could not find any allowed nodes in the Compilation Unit"); }
        System.out.println("EXPRESSIONS: " + expressions);

        // Pick at random a hashMap entry
        String randomType = (String) expressions.keySet().toArray()[GPHelpers.randomIndex(expressions.keySet().size())]; // we randomise this hashmap keys
        System.out.println("RANDOM TYPE: " + randomType);

        // Assign newExpr
        newExpr.setLeft(expressions.get(randomType).get(GPHelpers.randomIndex(expressions.get(randomType).size())));
        newExpr.setOperator(relationOperators[GPHelpers.randomIndex(relationOperators.length)]);
        newExpr.setRight(expressions.get(randomType).get(GPHelpers.randomIndex(expressions.get(randomType).size())));

        return newExpr;
    }

    private static Expression getSingleNameExpression(CompilationUnit cu, Expression randomExpression) {
        NodeList<Expression> booleanTypes = SVM.getRequiredTypes(randomExpression, Collections.singletonList("boolean"));
        System.out.println("BOOLEAN TYPES: " + booleanTypes);

        if (booleanTypes != null && booleanTypes.size() > 0)  return booleanTypes.get(GPHelpers.randomIndex(booleanTypes.size()));
        throw new NullPointerException("No `Valid Boolean Types` found in the Compilation Unit");
    }

    public static HashMap<String, List<Expression>> getExpressionsInSpecificClassAndMethodWithoutType(Node node) {
        List<Map.Entry<String, Expression>> expressionEntries = new ArrayList<>();

        node.findAncestor(ClassOrInterfaceDeclaration.class).ifPresent(coid -> {
            coid.findAll(FieldDeclaration.class).forEach(fd -> fd.getVariables().forEach(vd -> expressionEntries.add(new AbstractMap.SimpleEntry<>(vd.resolve().getType().describe(), vd.getNameAsExpression().clone()))));
            coid.findAll(EnumDeclaration.class).forEach(ed -> { for (EnumConstantDeclaration enumConstant : ed.getEntries()) { expressionEntries.add(new AbstractMap.SimpleEntry<>(ed.resolve().getClassName(), new FieldAccessExpr().setScope(ed.getNameAsExpression()).setName(enumConstant.getName()))); }});
        });

        node.findAncestor(MethodDeclaration.class).ifPresent(md -> {
            md.findAll(MethodCallExpr.class).forEach(mce -> { if (compareLineNumbers(mce.getBegin(), node.getBegin())) { expressionEntries.add(new AbstractMap.SimpleEntry<>(mce.resolve().getReturnType().describe(), mce.clone())); }});
            md.findAll(VariableDeclarator.class).forEach(vd -> { if (compareLineNumbers(vd.getBegin(), node.getBegin())) { expressionEntries.add(new AbstractMap.SimpleEntry<>(vd.resolve().getType().describe(), vd.getNameAsExpression().clone())); }});
            md.findAll(ObjectCreationExpr.class).forEach(oce -> { if (compareLineNumbers(oce.getBegin(), node.getBegin())) { expressionEntries.add(new AbstractMap.SimpleEntry<>(oce.resolve().getClassName(), oce.clone())); }});
            md.findAll(FieldAccessExpr.class).forEach(fae -> { if (compareLineNumbers(fae.getBegin(), node.getBegin())) { expressionEntries.add(new AbstractMap.SimpleEntry<>(fae.resolve().getType().describe(), fae.clone())); }});
            md.getParameters().forEach(parameter -> expressionEntries.add(new AbstractMap.SimpleEntry<>(parameter.resolve().getType().describe(), parameter.getNameAsExpression().clone())));
        });

        // Group together into HashMap<String, List<Expression>>
        HashMap<String, List<Expression>> expressions = new HashMap<>();

        for (Map.Entry<String, Expression> entry : expressionEntries) {
            expressions.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
            expressions.get(entry.getKey()).add(entry.getValue());
        }

        return expressions;
    }
}