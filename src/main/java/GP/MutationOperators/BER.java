package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.YamlPrinter;
import javassist.expr.Expr;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BER {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        // We have our cu object, we can now visit
        //VoidVisitor<HashMap<Node, Expression>> voidVisitor = new BAR.BooleanExprCollector();

        YamlPrinter printer = new YamlPrinter(true);
        //System.out.println(printer.output(program));

        HashMap<Node, Expression> booleanExpressions = new HashMap<>();
        //voidVisitor.visit(program, booleanExpressions);

        Node nodea = null;
        HashMap<Node, List<BinaryExpr>> binaryExpressions = new HashMap<>();
        for (Node node : booleanExpressions.keySet()) {
            if (getExpressionLength((BinaryExpr) booleanExpressions.get(node)) > 1) { binaryExpressions.put(node, recurseBinaryExpr(booleanExpressions.get(node))); }
            nodea = node;
        }

        // Go to the if loop...
        List<BinaryExpr> test = binaryExpressions.get(nodea);
        System.out.println(test.get(0));

        // Replace last condition!
        test.get(0).replace(test.get(1));



        //if (binaryExpressions.size() < 1) { throw new NullPointerException("The filtering of the expression " + booleanExpressions.values() + " reduces to zero."); }

        //System.out.println(booleanExpressions.get(expression));


        System.out.println(program);
        return program.clone();
    }

    public static int getExpressionLength(BinaryExpr expression) {
        List<BinaryExpr> expressions = recurseBinaryExpr(expression);
        return expressions.size();
    }

    public static List<BinaryExpr> recurseBinaryExpr(Node expression) {
        List<BinaryExpr> expressions = new ArrayList<>();

        if(expression instanceof BinaryExpr) expressions.add((BinaryExpr) expression);

        // Recurse into each child node until a child is not a binary expr
        for (Node node : expression.getChildNodes()) {
            if (node instanceof BinaryExpr) expressions.addAll(recurseBinaryExpr(node));
        }

        return expressions;
    }
}