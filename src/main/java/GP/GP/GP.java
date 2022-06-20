package main.java.GP.GP;

import Util.CSVOutput;
import com.github.javaparser.ast.CompilationUnit;
import main.java.GP.GP.GPThread;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import main.java.Util.ParserRunner;
import main.java.Util.ProjectPaths;
import main.java.Util.ShellProcessBuilder;

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
    int numberOfTestCases;

    public GP(CompilationUnit ast, Class<?> mutationOperator, int numberOfTestCases, String programPath, String buggyProgramPath) {
        this.fitnessOfFittestProgram = 0;
        this.numberOfTestCases = numberOfTestCases;
        this.buggyProgramPath = buggyProgramPath;
        this.mutationOperator = mutationOperator;
        this.populationSize = ParserRunner.gp.populationSize;
        this.fittestProgram = ast;
        this.mutationRate = ParserRunner.gp.mutationRate;
        this.generations = ParserRunner.gp.generations;
        this.programPath = programPath;
        this.ast = ast;
    }

    public void initialisePopulation() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        for (int count=0; count<populationSize; count++) {
            population.add(applyMutation());
        }
    }

    public Boolean threadsRunning(ArrayList<GPThread> threads) {
        for (GPThread thread : threads) {
            if (thread.gpThread.isAlive()) { return true; }
        }

        return false;
    }

    public CompilationUnit applyMutation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (CompilationUnit) mutationOperator.getMethod("mutate", CompilationUnit.class).invoke(mutationOperator, ast);
    }

    public ArrayList<CompilationUnit> main() throws Exception {
        initialisePopulation();
        ArrayList<CompilationUnit> patches = new ArrayList<>();

        int generation = 0;
        while (generation < generations) {
            ArrayList<GPThread> threads = new ArrayList<>();

            int chunkSize = populationSize/ParserRunner.gp.numberOfThreads;
            int count = 0;
            for (int i=0; i<populationSize; i+=chunkSize) {
                threads.add(new GPThread(new ArrayList<>(population.subList(i, Math.min(populationSize, i + chunkSize))), numberOfTestCases, Integer.toString(count+1), programPath, buggyProgramPath));
                threads.get(count).start();
                count++;
            }

            while (threadsRunning(threads)) {}

            // addAll adds to end of the specified list, e.g. [1,2,3].addAll(4,5,6) => [1,2,3,4,5,6]
            ArrayList<Integer> fitnessResults = new ArrayList<>();
            for (GPThread thread : threads) {
                fitnessResults.addAll(thread.fitnessResults);
            }

            // Get index of the max value in the list
            int localMaxFitness = Collections.max(fitnessResults);

            if (localMaxFitness > fitnessOfFittestProgram) {
                fittestProgram = population.get(fitnessResults.indexOf(localMaxFitness));
                fitnessOfFittestProgram = localMaxFitness;
                if (fitnessOfFittestProgram == numberOfTestCases) {
                    System.out.println("Fix was found ");

                    // Check to make sure all test cases are passed
                    ProjectPaths.writeToFile(Paths.get(buggyProgramPath), fittestProgram.toString());
                    ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "compile", "-w", programPath});

                    ArrayList<String> testResults = main.java.Util.ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "test", "-w", programPath});
                    if (Character.getNumericValue(testResults.get(0).charAt(testResults.get(0).length() - 1)) != 0) { CSVOutput.passesAllTests += "Patch " + patches.size() + "fails"; }
                    
                    patches.add(fittestProgram);

                    // TODO: Need to think about multiple patches being found and how to handle!!
                    //
                }
            }

            //System.out.println("GENERATION: " + generation + " :: highest fitness value of " + fitnessOfFittestProgram + "/" + numberOfTestCases);

            //if (fitnessOfFittestProgram == numberOfTestCases) {
            //    System.out.println("Fix was found ");
            //    patches.add(fittestProgram);
            //    return patches;
            //}

            // Apply mutation based on probability
            for (int i=0; i<populationSize; i++) {
                if (Math.random() <= mutationRate) {
                    population.set(i, applyMutation());
                }
            }

            generation++;
        }

        System.out.println("No fix was created in " + generations);
        patches.add(ast);
        return patches;
        //return null;
    }
}