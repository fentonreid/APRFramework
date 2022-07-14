package GP.MutationOperators;

import com.github.javaparser.ast.CompilationUnit;

public final class BAR {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        // To call BEM
        if (Math.random() < 0.5) { return BEM.mutate(program); }

        // To call BER
        return BER.mutate(program);
    }
}