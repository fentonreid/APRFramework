package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import com.github.javaparser.ast.CompilationUnit;

public final class BAR {
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // To call BEM
        if (Math.random() < 0.5) { return BEM.mutate(program); }

        // To call BER
        return BER.mutate(program);
    }
}