package GP.MutationOperators;

import com.github.javaparser.ast.CompilationUnit;

public final class BER {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        // Remove
        if (Math.random() < 0.5) {
            return BERExpansion.mutate(program);
        }
        
        // Relocate
        return BERExpansion.mutate(program);
    }
}