package GP.GP;

import Util.CSVOutput;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import Util.ParserRunner;
import Util.ShellProcessBuilder;

/**
 * The GP class contains the implementation details for initialising, mutating and generating the fitness values of the population per generation.
 */
public final class GP {
    String programPath;
    String buggyProgramPath;
    int populationSize;
    int generations;
    double mutationRate;
    CompilationUnit ast;
    Class<?> mutationOperator;
    ArrayList<CompilationUnit> population = new ArrayList<>();
    CompilationUnit fittestProgram;
    int fitnessOfFittestProgram;
    int chunkSize;

    /**
     * The constructor for the GP class, initialises the field variables; ast, mutationOperator, programPath and buggyProgramPath.
     * The population is also initialised to the population size filled with the original buggy defects4j program.
     *
     * @param ast                   The original ast of the buggy defects4j program
     * @param mutationOperator      The mutationOperator class to be used for mutation
     * @param programPath           The programPath to the defects4j bug on the filesystem
     * @param buggyProgramPath      The buggy path points to the specific buggy Java file
     */
    public GP(CompilationUnit ast, Class<?> mutationOperator, String programPath, String buggyProgramPath) {
        this.fitnessOfFittestProgram = 10_000;
        this.buggyProgramPath = buggyProgramPath;
        this.mutationOperator = mutationOperator;
        this.populationSize = ParserRunner.gp.populationSize;
        this.fittestProgram = ast.clone();
        this.mutationRate = ParserRunner.gp.mutationRate;
        this.generations = ParserRunner.gp.generations;
        this.programPath = programPath;
        this.ast = ast.clone();
        this.chunkSize = populationSize/ParserRunner.gp.numberOfThreads;

        // Initialise arraylist population with original ast to population size
        for (int count=0; count<populationSize; count++) {
            population.add(ast.clone());
        }
    }

    /**
     * The fitness of the population is calculated and returned as an ArrayList of integer where the size and index of the fitnessResults and population match.
     * The fitness of the population is deferred to the GPThread class with the population size being split evenly to the number of threads specified in the YAML configuration file.
     * The subpopulation for each thread is combined back into the original population.
     *
     * @return An ArrayList of Integer with the fitness of each program combined from each thread
     */
    public ArrayList<Integer> getFitnessOfPopulation() {
        ArrayList<GPThread> threads = new ArrayList<>();

        int count = 0;
        for (int i=0; i<populationSize; i+=chunkSize) {
            // Split the population evenly into parts and start each thread
            threads.add(new GPThread(new ArrayList<>(population.subList(i, Math.min(populationSize, i + chunkSize))), Integer.toString(count+1), programPath, buggyProgramPath));
            threads.get(count).start();
            count++;
        }

        // While one or many threads are running keep waiting
        while (gpThreadRunning(threads)) {}

        // Combine the results of all fitness results into one ArrayList
        ArrayList<Integer> fitnessResults = new ArrayList<>();
        for (GPThread thread : threads) {
            fitnessResults.addAll(thread.fitnessResults);
        }

        return fitnessResults;
    }

    /**
     * The GP population is mutated and the new mutated population is set.
     * The mutation of the population is deferred to the MutationThread class with the population size being split evenly to the number of threads specified in the YAML configuration file.
     * The subpopulation for each thread is combined back into the original population.
     *
     * @return An ArrayList of Integer with the fitness of each program combined from each thread
     */
    public void applyMutationToPopulation() {
        ArrayList<MutationThread> threads = new ArrayList<>();

        int count = 0;
        for (int i=0; i<populationSize; i+=chunkSize) {
            threads.add(new MutationThread(new ArrayList<>(population.subList(i, Math.min(populationSize, i + chunkSize))), ast.clone(), mutationOperator));
            threads.get(count).start();
            count++;
        }

        while (mutationThreadRunning(threads)) {}

        ArrayList<CompilationUnit> newPopulation = new ArrayList<>();
        for (MutationThread thread : threads) {
            newPopulation.addAll(thread.mutatedPopulation);
        }

        population = new ArrayList<>(newPopulation);
    }

    /**
     * A standard GP lifecycle is employed. Firstly, the GP is run per iteration with the population first being initialised, once initialised the
     * fitness values of all individuals in the population are calculated and if the smallest local value beats the global minimum then the global
     * is updated, a standard find minimum algorithm. If the exit criteria is met for this GP this means that a fitness of 0 has been found. From
     * this the generated patch has all its tests run to ensure it passes before being returned from this method.
     *
     * @return              Either null if no patch could be generated or the generated patch in an AST format
     * @throws Exception    The defects4j bug could not be compiled or tested
     */
    public CompilationUnit main() throws Exception {
        applyMutationToPopulation();

        int generation = 1;
        while (generation <= generations) {
            ArrayList<Integer> fitnessResults = new ArrayList<>(getFitnessOfPopulation());
            System.out.println("FITNESS RESULTS: " + fitnessResults);

            // Get index of the max value in the list
            int localMinFitness = Collections.min(fitnessResults);

            if (localMinFitness < fitnessOfFittestProgram) {
                fittestProgram = population.get(fitnessResults.indexOf(localMinFitness));
                fitnessOfFittestProgram = localMinFitness;

                if (fitnessOfFittestProgram == 0) {
                    System.out.println("Fix was found");

                    // Check to make sure all test cases are passed if not don't add to valid patches
                    Files.write(Paths.get(programPath + "/1/" +  buggyProgramPath), fittestProgram.toString().getBytes());
                    ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "compile", "-w", programPath+"/1/"}).waitFor();
                    ArrayList<String> testResults = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "test", "-w", programPath+"/1/"});

                    if (testResults.size() == 1) {
                        CSVOutput.patchesGenerated.add(generation);
                        return fittestProgram.clone();
                    }
                }
            }

            // High fitness values of 10_000, which is the default value given to mutations that fail have a 75% chance of being left unmodified
            for (int i=0; i<populationSize; i++) {
                if (fitnessResults.get(i) >= 10_000 && Math.random() < 0.75) {
                    population.set(i, ast.clone());
                }
            }

            System.out.println("GENERATION: " + generation + " :: lowest fitness value of " + fitnessOfFittestProgram);

            // Apply mutation based on probability
            applyMutationToPopulation();

            generation++;
        }

        System.out.println("No fix was created in " + generations);
        return null;
    }

    /**
     * This helper method determines if all mutation threads have finished successfully.
     *
     * @param threads   An ArrayList of MutationThreads for which all threads contained must die
     * @return          A boolean type, true if one/many threads are alive and false if none are alive
     */
    public Boolean mutationThreadRunning(ArrayList<MutationThread> threads) {
        for (MutationThread thread : threads) {
            if (thread.mutationThread.isAlive()) { return true; }
        }

        return false;
    }

    /**
     * This helper method determines if all mutation threads have finished successfully.
     *
     * @param threads   An ArrayList of GPThreads for which all threads contained must die
     * @return          A boolean type, true if one/many threads are alive and false if none are alive
     */
    public Boolean gpThreadRunning(ArrayList<GPThread> threads) {
        for (GPThread thread : threads) {
            if (thread.gpThread.isAlive()) { return true; }
        }

        return false;
    }
}