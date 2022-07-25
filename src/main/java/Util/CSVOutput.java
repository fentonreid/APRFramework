package Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import YAMLParser.Gp;

/**
 * The CSVOutput class creates the CSV output for each Defects4j bug that is processed by the GP.
 */
public final class CSVOutput {
    public static ArrayList<String[]> csvData;
    public static ArrayList<String[]> generalDetails;
    public static ArrayList<String[]> iterationDetails;
    public static ArrayList<Integer> patchesGenerated;
    public static String passesAllTests;

    /**
     * Clears the GP statistics for the next bug that is to be processed.
     */
    public static void reinitalise() {
        csvData = new ArrayList<>();
        generalDetails = new ArrayList<>();
        iterationDetails = new ArrayList<>();
        patchesGenerated = new ArrayList<>();
        passesAllTests = "";

        generalDetails.add(new String[]{"General Details", "", "Generations", "Population Size", "Mutation Rate", "Number Of Threads", "Estimated Compile Time (s)", "Estimated Test Time (s)", "Number of Relevant Test Classes", "Number of Total Test Classes"});
        iterationDetails.add(new String[]{"Iteration Breakdown", "", "Iteration", "Mutation Operator", "Patch found at generation", "All Tests Passed", "Total time taken (s)"});
    }

    /**
     * Adds general details of the Defects4j bug to the CSVOutput object to later be added.
     *
     * @param estimatedCompileTime      The estimated time it takes for the Defects4j bug to compile
     * @param estimatedTestTime         The estimated time for the relevant Defects4j bug to be tested
     * @param relevantTestClasses       The number of relevant test classes associated with the Defects4j bug
     * @param totalTestClasses          The number of total test classes associated with the Defects4j bug
     */
    public static void addGeneralDetailsEntry(String estimatedCompileTime, String estimatedTestTime, int relevantTestClasses, int totalTestClasses) {
        Gp gpObj = ParserRunner.gp;
        generalDetails.add(new String[]{"", "", String.valueOf(gpObj.generations),
                                  String.valueOf(gpObj.populationSize),
                                  String.valueOf(gpObj.mutationRate),
                                  String.valueOf(gpObj.numberOfThreads),
                                  String.valueOf(estimatedCompileTime),
                                  String.valueOf(estimatedTestTime),
                                  String.valueOf(relevantTestClasses),
                                  String.valueOf(totalTestClasses)
        });
    }

    /**
     * Adds iteration details for the patch that was generated to be added to the CSVOutput object to later be added.
     *
     * @param iteration                The iteration that the patch was solved on
     * @param mutationOperator         The mutation operator that was used to create the patch
     * @param numberOfPatchesFound     The number of patches that were added to the patch
     * @param totalTimeTaken           The total time taken to generate the patch
     */
    public static void addIterationBreakdownEntry(int iteration, String mutationOperator, int numberOfPatchesFound, String totalTimeTaken) {
        String iterationString = String.valueOf(iteration);
        for (String[] line : iterationDetails) {
            if (line.length > 3 && line[2].equals(iterationString)) {
                iterationString = "";
                break;
            }
        }

        if (iterationString.equals(String.valueOf(iteration)) && iterationDetails.size() != 1) { iterationDetails.add(new String[]{""}); }

        iterationDetails.add(new String[]{"", "", iterationString,mutationOperator,
                                  String.valueOf(numberOfPatchesFound),
                                  passesAllTests.equals("") ? "YES" : passesAllTests,
                                  String.valueOf(totalTimeTaken)
        });

        passesAllTests = "";
    }

    /**
     * Add a number of line breaks to the CSV output.
     *
     * @param lineBreakCount    The number of new lines to add to the CSV output
     */
    public static void addLineBreak(int lineBreakCount) {
        for (int i=0; i<lineBreakCount; i++)
            csvData.add(new String[]{""});
    }

    /**
     *  Combine the general and iteration details of the current Defects4j bug together and write to the output path.
     *
     * @param outputPath    The path for the CSV to be saved to
     * @throws Exception    The output path could not be written too
     */
    public static void generateCSV(Path outputPath) throws Exception {
        csvData.addAll(generalDetails);
        addLineBreak(1);
        csvData.addAll(iterationDetails);

        // Write CSV file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            for (String[] line : csvData) {
                bw.write(String.join(",", Arrays.asList(line)));
                bw.newLine();
            }
        } catch (Exception ex) { throw new Exception("Could not write '" + outputPath.getFileName() + "' to " + outputPath); }
    }

    /**
     * Time properties are formatted to ensure they have only two decimal places.
     *
     * @param start     The start time as a long type
     * @param end       The end time as a long type
     * @return          A string of the difference between the start and end time with two decimal places
     */
    public static String formatTime(long start, long end) {
        return String.format("%.2f", ((end - start) / 1_000_000_000.0));
    }
}