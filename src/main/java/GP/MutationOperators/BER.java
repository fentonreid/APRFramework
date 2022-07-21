package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import com.github.javaparser.ast.CompilationUnit;

public final class BER {
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // Expand boolean expressions
        if (Math.random() < 0.5) {
            return BERExpansion.mutate(program);
        }

        // Remove boolean expressions
        return BERReduction.mutate(program);
    }
}