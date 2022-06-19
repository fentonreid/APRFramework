package main.java.GP.GP;

import main.java.Util.ProjectPaths;
import main.java.GP.GP.AbstractSyntaxTree;
import main.java.GP.GP.GP;
import com.github.javaparser.ast.CompilationUnit;
import main.java.Util.ShellProcessBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import main.java.Util.ParserRunner;
import org.apache.commons.io.FileUtils;

public final class GPRunner {
    public static void main() throws Exception {
        for (Map.Entry<String, HashSet<Integer>> entry : ParserRunner.defects4j.selectedTestCases.entrySet()) {
            String identifier = entry.getKey();

            for (int bid : entry.getValue()) {
                String checkoutFolderBase = "/tmp/" + identifier + "_" + bid + "/";

                for (int threadCount=1; threadCount<= ParserRunner.gp.numberOfThreads;  threadCount++) {
                    String checkoutPath = checkoutFolderBase + threadCount;

                    if (threadCount > 1) {
                        FileUtils.copyDirectory(new File(checkoutFolderBase + "1"), new File(checkoutPath));
                    } else {
                        Process finishedProcess = ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", identifier, "-v", bid + "b", "-w", checkoutPath});
                        if (new InputStreamReader(finishedProcess.getInputStream()).read() != -1) { throw new Exception("Error when trying to checkout '" + identifier + "' with a bug id of '" + bid + " at thread " + ParserRunner.gp.numberOfThreads + "'"); }
                        if (ParserRunner.gp.numberOfThreads > 1) {
                            ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "compile", "-w", checkoutPath}).wait();
                            ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "test", "-r", "-w", checkoutPath}).wait();
                        }
                    }

                    if(!Files.exists(Paths.get(checkoutPath))) { throw new Exception("Could not checkout '" + checkoutPath + "' properly"); }
                }

                String checkoutPath = checkoutFolderBase + "1";

                // Get the number of test cases
                int numberOfTestCases = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "export", "-p", "tests.relevant", "-w", checkoutPath}).size();

                // Get the buggy file and save as AST representation
                Path buggyFilePath = ProjectPaths.getBuggyProgramPath(checkoutPath);
                CompilationUnit buggyAST = AbstractSyntaxTree.generateAST(Paths.get(checkoutPath + buggyFilePath));

                for (String mutationOperator : ParserRunner.gp.mutationOperators) {
                    System.out.println("MUTATION OPERATOR: " + mutationOperator);
                    ArrayList<CompilationUnit> patches = new ArrayList<>();

                    for (int i = 1; i <= ParserRunner.gp.iterationsPerBug; i++) {
                        System.out.println("ON iteration: " + i);
                        long start = System.nanoTime();
                        patches.addAll(new GP(buggyAST, Class.forName("main.java.GP.MutationOperators." + mutationOperator), numberOfTestCases, checkoutFolderBase, buggyFilePath.toString()).main());
                        long end = System.nanoTime();

                        //System.out.println("\n\n");
                        //System.out.printf("%.2f", ((end - start) / 1_000_000_000.0));
                        //System.out.print("Time taken to generate \n");
                    }

                    // Copy patches for the current bug into /output/{identifier}_{bid}/{mutationOperator}/{patchNumber}
                    ProjectPaths.saveBugsToFileSystem(identifier, bid, mutationOperator, patches);
                }

                // Copy fixed bug into /output and delete the /tmp/{identifier}_{bid} directory
                ProjectPaths.copyFile(ProjectPaths.getFixedProgramPath(identifier, bid), Paths.get("/output/" + identifier + "_" + bid + "/" + "Patch" + bid));
                FileUtils.deleteDirectory(new File(checkoutFolderBase));
            }
        }
    }
}