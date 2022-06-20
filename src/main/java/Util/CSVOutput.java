package Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import main.java.Util.ParserRunner;
import main.java.YAMLParser.Gp;

public final class CSVOutput {
    public static ArrayList<String[]> csvData;
    public static ArrayList<String[]> generalDetails;
    public static ArrayList<String[]> iterationDetails;
    public static ArrayList<Integer> patchesGenerated;
    public static String passesAllTests;

    public static void reinitalise() {
        csvData = new ArrayList<>();
        generalDetails = new ArrayList<>();
        iterationDetails = new ArrayList<>();
        patchesGenerated = new ArrayList<>();
        passesAllTests = "";

        generalDetails.add(new String[]{"General Details", "", "Generations", "Population Size", "Mutation Rate", "Number Of Threads", "Estimated Compile Time (s)", "Estimated Test Time (s)", "Relevant Test Cases", "Total Test Cases"});
        iterationDetails.add(new String[]{"Iteration Breakdown", "", "Iteration", "Mutation Operator", "Patches generated" , "Patches found at generation", "All Tests Passed", "Total time taken (s)"});
    }

    public static void addGeneralDetailsEntry(String estimatedCompileTime, String estimatedTestTime, int relevantTestCases, int totalTestCases) {
        Gp gpObj = ParserRunner.gp;
        generalDetails.add(new String[]{"", "", String.valueOf(gpObj.generations),
                                  String.valueOf(gpObj.populationSize),
                                  String.valueOf(gpObj.mutationRate),
                                  String.valueOf(gpObj.numberOfThreads),
                                  String.valueOf(estimatedCompileTime),
                                  String.valueOf(estimatedTestTime),
                                  String.valueOf(relevantTestCases),
                                  String.valueOf(totalTestCases)
        });
    }

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
                                  patchesGenerated.toString(),
                                  passesAllTests.equals("") ? "YES" : passesAllTests,
                                  String.valueOf(totalTimeTaken)
        });

        patchesGenerated = new ArrayList<>();
        passesAllTests = "";
    }

    public static void addLineBreak(int lineBreakCount) {
        for (int i=0; i<lineBreakCount; i++)
            csvData.add(new String[]{""});
    }

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

    public static String formatTime(long start, long end) {
        return String.format("%.2f", ((end - start) / 1_000_000_000.0));
    }
}