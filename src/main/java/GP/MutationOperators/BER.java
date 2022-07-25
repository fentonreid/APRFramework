package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import com.github.javaparser.ast.CompilationUnit;

/**
 * The BER mutation provides the functionality of both the BER Expansion and BER Reduction mutations combined, with the end goal of this mutation to be able to add too and remove expressions found in the program.
 */
public final class BER {

    /**
     * (1) Determine which mutation to choose from, 50/50 for either BER Expansion or BER Reduction<br>
     * (2) Run the mutation and return the modified program back
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // Expand boolean expressions
        if (Math.random() < 0.5) {
            return BERExpansion.mutate(program);
        }

        // Remove boolean expressions
        return BERReduction.mutate(program);
    }
}