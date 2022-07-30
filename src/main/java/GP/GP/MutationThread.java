package GP.GP;

import GP.MutationOperators.GNR;
import Util.ParserRunner;
import com.github.javaparser.ast.CompilationUnit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The MutationThread class handles the application of the mutation to a subset of the population as defined by the population size / number of threads.
 */
public final class MutationThread extends Thread {
    public Thread mutationThread;
    public ArrayList<CompilationUnit> population;
    public ArrayList<CompilationUnit> mutatedPopulation = new ArrayList<>();
    private final CompilationUnit ast;
    private final Class<?> mutationOperator;

    /**
     * The constructor for the MutationThread class, ensures that the public field variables; population, mutationThreadName, ast and mutationOperator are set.
     *
     * @param population            An ArrayList of Compilation Units representing a mutation of the original buggy Defects4j program
     * @param ast                   The original ast representation of the buggy Defects4j program
     * @param mutationOperator      The Class of the mutation operator that is to be used for mutation
     */
    public MutationThread(ArrayList<CompilationUnit> population, CompilationUnit ast, Class<?> mutationOperator) {
        this.mutationOperator = mutationOperator;
        this.population = population;
        this.ast = ast;
    }

    /**
     * Every program in the population is mutated using the specific mutation operator from the YAML configuration file and based on the probability set by the mutation rate.
     * If the program encounters an exception then the original ast is added to the new population.
     */
    public void run() {
        for (CompilationUnit program : population) {
            try {
                System.out.println("WORKED FINE!");
                if (Math.random() < ParserRunner.gp.mutationRate) {
                    mutatedPopulation.add((CompilationUnit) mutationOperator.getMethod("mutate", CompilationUnit.class).invoke(mutationOperator, program.clone()));
                }

            } catch (InvocationTargetException ite) {
                if (ite.getCause() instanceof UnmodifiedProgramException) { System.out.println("Unmodified Program Exception here:\t" + " " + ite.getCause().getMessage()); }
                else {
                    System.out.println("Tried to apply a mutation but it failed " + ite.getCause().getMessage() + Arrays.toString(ite.getStackTrace()) + ite.getLocalizedMessage()); }

                mutatedPopulation.add(ast.clone());

            } catch (Exception ex) {
                throw new RuntimeException("Unknown error " + ex);
            }
        }
    }

    /**
     * Starts the MutationThread calling the Thread class to handle the lower level implementation required for the Thread to work.
     */
    public void start() {
        if (mutationThread == null) {
            mutationThread = new Thread(this);
            mutationThread.start();
        }
    }
}
