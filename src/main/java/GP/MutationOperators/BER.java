package GP.MutationOperators;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.stmt.*;

import java.util.ArrayList;
import java.util.List;

public final class BER {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        // Remove
        if (Math.random() < 0.5) {
            return BERAddition.mutate(program);
        }
        
        // Relocate
        return BERAddition.mutate(program);
    }
}