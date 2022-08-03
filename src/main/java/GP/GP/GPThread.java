package GP.GP;

import com.github.javaparser.ast.CompilationUnit;
import Util.ShellProcessBuilder;
import Util.ProjectPaths;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The GPThread class handles the fitness function calculation for a subset of the population as defined by the population size / number of threads.
 */
public final class GPThread extends Thread {
    public Thread gpThread;
    public String gpThreadName;
    public String programPath;
    public String buggyProgramPath;
    public ArrayList<Integer> fitnessResults;
    public ArrayList<CompilationUnit> population;

    /**
     * The constructor for the GPThread class, ensures that the public field variables; population, gpThreadName, programPath and buggyProgramPath are set.
     *
     * @param population            An ArrayList of Compilation Units representing a mutation of the original buggy Defects4j program
     * @param gpThreadName          The unique thread number used to represent part of the program path
     * @param programPath           The program path represents the path on the file system where the Defects4j bug has been checked out
     * @param buggyProgramPath      The path to the buggy program path, is a combination of the program path and buggy program path fields
     */
    public GPThread(ArrayList<CompilationUnit> population, String gpThreadName, String programPath, String buggyProgramPath) {
        this.population = population;
        this.gpThreadName = gpThreadName;
        this.programPath = programPath + gpThreadName;
        this.buggyProgramPath = this.programPath + buggyProgramPath;

        fitnessResults = new ArrayList<>();
    }

    /**
     * Each program in the population is copied over, compiled and then tested, the number of failed tests from calling the Defects4j test command is used as the fitness value.
     */
    public void run() {
        int currentProgramCount = 0;
        for (CompilationUnit program : population) {
            try {
                currentProgramCount++;

                ProjectPaths.writeToFile(Paths.get(buggyProgramPath), program.clone().toString());

                // Defects4j compile checked out program
                ShellProcessBuilder.runCommand(new String[]{"perl", "/defects4j/framework/bin/defects4j", "compile"}, new File(programPath));

                // Defects4j run tests on program
                String failingTests = ShellProcessBuilder.getFailingTestCases(new String[] {"perl", "/defects4j/framework/bin/defects4j", "test", "-r"}, programPath);

                if (failingTests == null) { fitnessResults.add(10_000); continue; }

                System.out.println(currentProgramCount + " :: NO ERROR HAS OCCURRED, (FAILING TESTS): " + failingTests);
                fitnessResults.add(Integer.valueOf(failingTests));

            } catch (IOException ex) {
                System.out.println("IO EXCEPTION HAS OCCURRED: " + ex);
                fitnessResults.add(10_000);

            } catch (Exception ex) {
                throw new RuntimeException(ex); }
        }
    }

    /**
     * Starts the gpThread specifying a unique name to the Thread.
     */
    public void start() {
        if (gpThread == null) {
            gpThread = new Thread(this, gpThreadName);
            gpThread.start();
        }
    }
}
