package GP;

import GP.GP.AbstractSyntaxTree;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.util.Objects;

public final class Util {
    public static Class<?> mutationOperator;

    /**
     * Try to resolve the current mutation 10_000 times, this is done to ensure a high likelihood that the mutation is not working rather than bad luck that the certain mutation could not be achieved.
     * Returns if a valid match between the pre- and post-mutation occurred.
     *
     * @param prePath       The path to the pre mutation for the specified test name
     * @param postPath      The path to the post mutation for the specified test name
     * @param testName      The name of the test we fetch the pre- and post-mutations for
     * @return              A boolean type, if the resulting mutation program matches the actual program
     */
    public static boolean iterationMutationUntilResolved(String prePath, String postPath, String testName) throws Exception {
        // Get the pre- and post-files from the respective resources folder
        ClassLoader classLoader = Util.class.getClassLoader();

        CompilationUnit preProgram  = AbstractSyntaxTree.generateAST(new File(Objects.requireNonNull(classLoader.getResource( prePath + testName + ".java")).getFile()).toPath(), "", "");
        CompilationUnit postProgram = AbstractSyntaxTree.generateAST(new File(Objects.requireNonNull(classLoader.getResource( postPath + testName + ".java")).getFile()).toPath(), "", "");

        String actualPostProgram = removeCompilationUnitWhitespace(postProgram.clone());

        boolean resolved = false;
        int count = 0;
        while (count < 10_000) {
            String currentPostProgram;
            try {
                currentPostProgram = removeCompilationUnitWhitespace((CompilationUnit) mutationOperator.getMethod("mutate", CompilationUnit.class).invoke(mutationOperator, preProgram.clone()));
            } catch (java.lang.reflect.InvocationTargetException ex) { currentPostProgram = preProgram.clone().toString(); }

            if (actualPostProgram.equals(currentPostProgram)) {
                resolved = true;
                break;
            }

            count++;
        }


        return resolved;
    }
    public static String removeCompilationUnitWhitespace(CompilationUnit program) {
        // Remove all double new lines (/n/n) to just /n, this means if the mutation adds or removes whitespace in different ways it can still be resolved correctly
        return program.toString().replaceAll("\n\n", "\n");
    }
}
