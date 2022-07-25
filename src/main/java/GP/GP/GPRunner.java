package GP.GP;

import Util.*;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * The GPRunner class ensures the correct execution of the GP based on the parameters passed by an individual from the YAML configuration file.
 * For each Defects4j bug selected the GP is run a certain number of iterations for each mutation operator selected and a summary csv, Defects4j patch and generated GP patches are saved to the /output folder.
 */
public final class GPRunner {

    /**
     * The Genetic program for each mutation operator for each number of iterations is performed.
     * The compile and test times are recorded and any generated patches are uploaded to the Vue website.
     * Furthermore, the bug output is saved to the /output folder with a summary of the GP hyper-parameters and iteration details for each mutation operator generated also
     *
     * @throws Exception The Defects4j bug could not be checked out or the generated patch could not be uploaded to Firebase
     */
    public static void main() throws Exception {
        long startCompileTime = 0;
        long endCompileTime = 0;
        long startTestTime = 0;
        long endTestTime = 0;

        for (Map.Entry<String, HashSet<Integer>> entry : ParserRunner.defects4j.selectedTestCases.entrySet()) {
            String identifier = entry.getKey();

            for (int bid : entry.getValue()) {
                CSVOutput.reinitalise();

                String checkoutFolderBase = "/tmp/checkout/" + identifier + "_" + bid + "/";

                for (int threadCount=1; threadCount<= ParserRunner.gp.numberOfThreads;  threadCount++) {
                    String checkoutPath = checkoutFolderBase + threadCount;

                    if (threadCount > 1) {
                        FileUtils.copyDirectory(new File(checkoutFolderBase + "1"), new File(checkoutPath));
                    } else {
                        int exitCode = ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", identifier, "-v", bid + "b", "-w", checkoutPath}).waitFor();
                        if (exitCode != 0) { throw new Exception("Error could not compile the program properly"); }

                        // Cache files for quicker processing
                        startCompileTime = System.nanoTime();
                        ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "compile", "-w", checkoutPath}).waitFor();
                        endCompileTime = System.nanoTime();

                        startTestTime = System.nanoTime();
                        ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "test", "-r", "-w", checkoutPath}).waitFor();
                        endTestTime = System.nanoTime();
                    }

                    if (!Files.exists(Paths.get(checkoutPath))) { throw new Exception("Could not checkout '" + checkoutPath + "' properly"); }
                }

                String checkoutPath = checkoutFolderBase + "1";

                int numberOfTestClasses = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "export", "-p", "tests.relevant", "-w", checkoutPath}).size();
                int numberOfTotalTestClasses = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "export", "-p", "tests.all", "-w", checkoutPath}).size();

                // Adding general details to CSVOutput
                CSVOutput.addGeneralDetailsEntry(CSVOutput.formatTime(startCompileTime/2, endCompileTime/2), CSVOutput.formatTime(startTestTime/2, endTestTime/2), numberOfTestClasses, numberOfTotalTestClasses);

                // Get the buggy file and source directory for symbol resolving and save as AST representation
                Path buggyFilePath = ProjectPaths.getBuggyProgramPath(checkoutPath);
                String sourceDirectory = ProjectPaths.getSourceDirectoryPath(checkoutPath);

                for (String mutationOperator : ParserRunner.gp.mutationOperators) {
                    ArrayList<CompilationUnit> patches = new ArrayList<>();

                    for (int i = 1; i <= ParserRunner.gp.iterationsPerBug; i++) {
                        long startIterationTime = System.nanoTime();
                        CompilationUnit buggyAST = AbstractSyntaxTree.generateAST(Paths.get(checkoutPath + buggyFilePath), sourceDirectory);

                        CompilationUnit currentIterationPatch = new GP(buggyAST, Class.forName("GP.MutationOperators." + mutationOperator), checkoutFolderBase, buggyFilePath.toString()).main();
                        if (currentIterationPatch != null) {
                            patches.add(currentIterationPatch);

                            // Prepare parameters for JSON payload to firebase
                            Map<String, Object> params = new HashMap<>();
                            params.put("patchId", patches.size());
                            params.put("identifier", identifier);
                            params.put("bid", bid);
                            params.put("actualPatch", currentIterationPatch.toString().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
                            // This has to change :p
                            params.put("gpPatch", FileUtils.readFileToString(ProjectPaths.getFixedProgramPath(identifier, bid).toFile(), "UTF-8").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
                            params.put("overfitness", "Unassigned");

                            // Upload patch to Firebase
                            try { Firebase.UploadPatchToFirebase("generatedpatches.json", params); }
                            catch (Exception ex) { System.out.println("Failed to upload generated patch to firebase: " + identifier + " " + bid); }
                        }
                        long endIterationTime = System.nanoTime();

                        CSVOutput.addIterationBreakdownEntry(i, mutationOperator, 1, CSVOutput.formatTime(startIterationTime, endIterationTime));
                    }

                    // Copy patches for the current bug into /output/{identifier}_{bid}/{mutationOperator}/{patchNumber}
                    ProjectPaths.saveBugsToFileSystem(identifier, bid, mutationOperator, patches);
                }

                // Generate CSV output into /output/{identifier}_{bid}/{SummaryCSV property}.csv
                CSVOutput.generateCSV(Paths.get("/output/" + identifier + "_" + bid + "/" + ParserRunner.output.summaryCSV + ".csv"));

                // Copy fixed bug into /output
                ProjectPaths.copyFile(ProjectPaths.getFixedProgramPath(identifier, bid), Paths.get("/output/" + identifier + "_" + bid + "/Defects4J_Validated_Patch"));

                //Delete the /tmp/{identifier}_{bid} directory
                FileUtils.deleteDirectory(new File(checkoutFolderBase));
            }
        }
    }
}