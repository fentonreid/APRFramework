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

       /*
       IDEA:
            => Find base child
            => Get the parent
            => Replace parent with other base child
         */
        
        //List<BinaryExpr> expressions = new ArrayList<>(program.findAll(BinaryExpr.class).stream().filter(BER::getBinaryExprChildren).collect(Collectors.toList()));
        List<BinaryExpr> expressions = new ArrayList<>(program.findAll(BinaryExpr.class));

        System.out.println(expressions);


        // -- Get boolean statements
        // -- Keep if they are size one or more
        // --

        //if (binaryExpressions.size() < 1) { throw new NullPointerException("The filtering of the expression " + booleanExpressions.values() + " reduces to zero."); }


        System.out.println(program);
        return program.clone();
    }

    public static boolean getBinaryExprChildren(BinaryExpr binaryExpr) {
        return binaryExpr.getLeft().getClass().getSimpleName().equals("BinaryExpr") && binaryExpr.getRight().getClass().getSimpleName().equals("BinaryExpr");
    }
}