package main.java.GP.MutationOperators;

import com.github.javaparser.ast.CompilationUnit;

public final class MutationOperator1 {
    public static CompilationUnit mutate(CompilationUnit program) {
        return program.clone();
    }
}