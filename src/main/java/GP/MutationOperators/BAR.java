package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import java.util.*;

public final class BAR {
    private static final BinaryExpr.Operator[] relationOperators = new BinaryExpr.Operator[]{ BinaryExpr.Operator.LESS, BinaryExpr.Operator.LESS_EQUALS, BinaryExpr.Operator.GREATER, BinaryExpr.Operator.GREATER_EQUALS, BinaryExpr.Operator.EQUALS, BinaryExpr.Operator.NOT_EQUALS };
    private static final BinaryExpr.Operator[] booleanOperators  = new BinaryExpr.Operator[]{ BinaryExpr.Operator.OR, BinaryExpr.Operator.AND};
    private static final UnaryExpr.Operator[] unaryOperators  = new UnaryExpr.Operator[]{ UnaryExpr.Operator.LOGICAL_COMPLEMENT};
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {

        //YamlPrinter printer = new YamlPrinter(true);
        //System.out.println(printer.output(program));

        // BinaryFlip
        // BooleanLiteralFlip
        // Unary NOR Flip
        // SWITCH ON INSTANCE e.g. binaryEXPR, unary or booleanliteral -> include specifics in here

        // ! is a LOGICAL_COMPLEMENT AND IS UNDER UNARYEXPR

//        // -- UNARY EXPRESSIONS --
//        List<UnaryExpr> unaryExprs = new ArrayList<>(program.findAll(UnaryExpr.class));
//
//        program.findAll(UnaryExpr.class).forEach(unary -> {
//            if (Arrays.asList(unaryOperators).contains(unary.getOperator())) {
//                unaryExprs.add(unary);
//            }
//        });
//
//        if(unaryExprs.size() == 0) { throw new NullPointerException("No unary expressions found in the given CompilationUnit"); }
//
//        UnaryExpr unaryExpr = unaryExprs.get(GPHelpers.randomIndex(unaryExprs.size()));
//        unaryExpr.replace(unaryExpr.getExpression()); // Remove the unary

        /*// -- BOOLEAN LITERAL EXPRESSIONS --
        List<BooleanLiteralExpr> booleanLiteralExprs = new ArrayList<>(program.findAll(BooleanLiteralExpr.class));
        if(booleanLiteralExprs.size() == 0) { throw new NullPointerException("No boolean literal expressions found in the given CompilationUnit"); }

        BooleanLiteralExpr booleanLiteralExpr = booleanLiteralExprs.get(GPHelpers.randomIndex(booleanLiteralExprs.size()));
        booleanLiteralExpr.setValue(!booleanLiteralExpr.getValue());*/


        // -- FLIP BINARY EXPRESSIONS --
        // All binary expressions -> e.g. x > 5 && y < 4 => returns => [x>5 && y<4, x<5, y<4]
        List<BinaryExpr> binaryExprs = new ArrayList<>(program.findAll(BinaryExpr.class));
        if(binaryExprs.size() == 0) { throw new NullPointerException("No binary expressions found in the given CompilationUnit"); }

        BinaryExpr booleanExpression = binaryExprs.get(GPHelpers.randomIndex(binaryExprs.size()));
        BinaryExpr.Operator booleanExpressionOperator = booleanExpression.getOperator();

        // Add random unary expression to existing expressions
        UnaryExpr unaryExpr = new UnaryExpr();
        unaryExpr.setExpression(new EnclosedExpr(booleanExpression.clone()));
        unaryExpr.setOperator(unaryOperators[GPHelpers.randomIndex(booleanOperators.length)]);

        booleanExpression.replace(unaryExpr);

        /*// Switches relation and boolean operators
        if (Arrays.asList(relationOperators).contains(booleanExpressionOperator)) {
            booleanExpression.setOperator(relationOperators[GPHelpers.randomIndex(relationOperators.length)]);
        } else if (Arrays.asList(booleanOperators).contains(booleanExpressionOperator)) {
            booleanExpression.setOperator(booleanOperators[GPHelpers.randomIndex(booleanOperators.length)]);
        } else {
            throw new Exception("This operator is not supported yet");
        }*/

        // Choices made
        // -- Get all unaryExpr and remove, !                                      OR::
        // -- Add unary ! operator e.g. add to beginning of binary expression      OR::

        System.out.println("\n\n");
        System.out.println(program);

        return program.clone();
    }
}