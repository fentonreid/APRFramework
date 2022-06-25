package GP.GP;

import Util.CSVOutput;
import Util.ProjectPaths;
import com.github.javaparser.ast.CompilationUnit;
import Util.ShellProcessBuilder;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import Util.ParserRunner;
import org.apache.commons.io.FileUtils;

public final class GPRunner {
    public static void main() throws Exception {
        long startCompileTime = 0;
        long endCompileTime = 0;
        long startTestTime = 0;
        long endTestTime = 0;

        for (Map.Entry<String, HashSet<Integer>> entry : ParserRunner.defects4j.selectedTestCases.entrySet()) {
            String identifier = entry.getKey();

            for (int bid : entry.getValue()) {
                CSVOutput.reinitalise();

                String checkoutFolderBase = "/tmp/" + identifier + "_" + bid + "/";

                for (int threadCount=1; threadCount<= ParserRunner.gp.numberOfThreads;  threadCount++) {
                    String checkoutPath = checkoutFolderBase + threadCount;

                    if (threadCount > 1) {
                        FileUtils.copyDirectory(new File(checkoutFolderBase + "1"), new File(checkoutPath));
                    } else {
                        Process finishedProcess = ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", identifier, "-v", bid + "b", "-w", checkoutPath});
                        if (new InputStreamReader(finishedProcess.getInputStream()).read() != -1) { throw new Exception("Error when trying to checkout '" + identifier + "' with a bug id of '" + bid + " at thread " + ParserRunner.gp.numberOfThreads + "'"); }

                        // Cache files for quicker processing
                        startCompileTime = System.nanoTime();
                        ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "compile", "-w", checkoutPath});
                        endCompileTime = System.nanoTime();

                        startTestTime = System.nanoTime();
                        ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "test", "-r", "-w", checkoutPath});
                        endTestTime = System.nanoTime();
                    }

                    if (!Files.exists(Paths.get(checkoutPath))) {
                        throw new Exception("Could not checkout '" + checkoutPath + "' properly");
                    }
                }

                String checkoutPath = checkoutFolderBase + "1";

                // Get the number of test cases
                int numberOfTestCases = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "export", "-p", "tests.relevant", "-w", checkoutPath}).size();
                int numberOfTotalTestCases = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "export", "-p", "tests.all", "-w", checkoutPath}).size();

                // Adding general details to CSVOutput
                CSVOutput.addGeneralDetailsEntry(CSVOutput.formatTime(startCompileTime/2, endCompileTime/2), CSVOutput.formatTime(startTestTime/2, endTestTime/2), numberOfTestCases, numberOfTotalTestCases);

                // Get the buggy file and save as AST representation
                Path buggyFilePath = ProjectPaths.getBuggyProgramPath(checkoutPath);
                CompilationUnit buggyAST = AbstractSyntaxTree.generateAST(Paths.get(checkoutPath + buggyFilePath));

                for (String mutationOperator : ParserRunner.gp.mutationOperators) {
                    ArrayList<CompilationUnit> patches = new ArrayList<>();

                    for (int i = 1; i <= ParserRunner.gp.iterationsPerBug; i++) {
                        long startIterationTime = System.nanoTime();
                        ArrayList<CompilationUnit> currentIterationPatches = new GP(buggyAST, Class.forName("GP.MutationOperators." + mutationOperator), numberOfTestCases, checkoutFolderBase, buggyFilePath.toString()).main();
                        if(currentIterationPatches.size() > 1) { patches.addAll(currentIterationPatches); }
                        long endIterationTime = System.nanoTime();

                        CSVOutput.addIterationBreakdownEntry(i, mutationOperator, currentIterationPatches.size(), CSVOutput.formatTime(startIterationTime, endIterationTime));
                    }

                    // Copy patches for the current bug into /output/{identifier}_{bid}/{mutationOperator}/{patchNumber}
                    ProjectPaths.saveBugsToFileSystem(identifier, bid, mutationOperator, patches);
                }

                // Generate CSV output into /output/{identifier}_{bid}/{SummaryCSV property}.csv
                CSVOutput.generateCSV(Paths.get("/output/" + identifier + "_" + bid + "/" + ParserRunner.output.summaryCSV + ".csv"));

                // Copy fixed bug into /output
                ProjectPaths.copyFile(ProjectPaths.getFixedProgramPath(identifier, bid), Paths.get("/output/" + identifier + "_" + bid + "/" + "Patch" + bid));

                //Delete the /tmp/{identifier}_{bid} directory
                FileUtils.deleteDirectory(new File(checkoutFolderBase));
            }
        }
    }
}