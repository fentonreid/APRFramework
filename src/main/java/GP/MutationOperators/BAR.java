package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import com.github.javaparser.ast.CompilationUnit;

/**
 * The Boolean and Relational (BAR) mutation combines the functionality of both the BER and BEM mutations, with the mutation being able to; switch boolean and relational operators of expressions, add and remove negation to child expressions and remove parts of expressions also.
 */
public final class BAR {

    /**
     * (1) Determine which mutation to choose from, 50/50 for either Boolean Expression Manipulation (BEM) or Boolean expansion and reduction (BER) mutation<br>
     * (2) Run the mutation and return the modified program back
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {
        // To call BEM
        if (Math.random() < 0.5) { return BEM.mutate(program); }

        // To call BER
        return BER.mutate(program);
    }
}