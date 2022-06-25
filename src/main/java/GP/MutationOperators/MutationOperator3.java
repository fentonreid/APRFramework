package GP.MutationOperators;

import com.github.javaparser.ast.CompilationUnit;

public final class MutationOperator3 {
    public static CompilationUnit mutate(CompilationUnit program) {
        return program.clone();
    }
}