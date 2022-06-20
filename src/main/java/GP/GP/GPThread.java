package main.java.GP.GP;

import com.github.javaparser.ast.CompilationUnit;
import main.java.Util.ShellProcessBuilder;
import main.java.Util.ProjectPaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class GPThread extends Thread {
    public Thread gpThread;
    public String gpThreadName;
    public String programPath;
    public String buggyProgramPath;
    public ArrayList<Integer> fitnessResults;
    public ArrayList<CompilationUnit> population;
    public int numberOfTestCases;

    public GPThread(ArrayList<CompilationUnit> population, int numberOfTestCases, String gpThreadName, String programPath, String buggyProgramPath) {
        this.population = population;
        this.gpThreadName = gpThreadName;
        this.numberOfTestCases = numberOfTestCases;
        this.programPath = programPath + gpThreadName;
        this.buggyProgramPath = this.programPath + buggyProgramPath;

        System.out.println(buggyProgramPath);
        
        fitnessResults = new ArrayList<>();
    }

    public void run() {
        try {
            for (CompilationUnit program : population) {
                ProjectPaths.writeToFile(Paths.get(buggyProgramPath), program.toString());

                // Defects4j compile checked out program
                ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "compile", "-w", programPath});

                // Defects4j run tests on program
                long start = System.nanoTime();
                ArrayList<String> testResults = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "test", "-r", "-w", programPath});
                long end = System.nanoTime();

                //System.out.println("\n\n");
                //System.out.printf("%.2f", ((end - start) / 1_000_000_000.0));
                //System.out.print("Test TIME\n");
                
                int failedTests = Character.getNumericValue(testResults.get(0).charAt(testResults.get(0).length() - 1));
                fitnessResults.add(numberOfTestCases - failedTests);
            }

        } catch (IOException ex) { throw new RuntimeException("Could not copy into '" + buggyProgramPath + "' ");
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void start() {
        if (gpThread == null) {
            gpThread = new Thread(this, gpThreadName);
            gpThread.start();
        }
    }
}
