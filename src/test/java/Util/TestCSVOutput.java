package Util;

import YAMLParser.Gp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCSVOutput {

    @Test
    @DisplayName("CSV output")
    public void testCSVOutput() throws Exception {

        ParserRunner.gp = new Gp();
        ParserRunner.gp.generations = 1;
        ParserRunner.gp.populationSize = 1;
        ParserRunner.gp.mutationRate = 1.0;
        ParserRunner.gp.numberOfThreads = 1;

        System.out.println(ParserRunner.gp);

        // Add test entries to CSVData field of CSVOutput class
        CSVOutput.reinitalise();
        CSVOutput.addGeneralDetailsEntry("18.83", "10.13", 10, 13);
        System.out.println(CSVOutput.generalDetails);
        CSVOutput.addIterationBreakdownEntry(1, "BER", 1, "84.32");

        // Emulate the general and iteration details that should be created by the above method calls
        ArrayList<String[]> generalDetails = new ArrayList<>();
        generalDetails.add(new String[] { "General Details", "", "Generations", "Population Size", "Mutation Rate", "Number Of Threads", "Estimated Compile Time (s)", "Estimated Test Time (s)", "Number of Relevant Test Classes", "Number of Total Test Classes" });
        generalDetails.add(new String[] { "", "", "1", "1", "1.0", "1", "18.83", "10.13", "10", "13"});

        ArrayList<String[]> iterationDetails = new ArrayList<>();
        iterationDetails.add(new String[]{ "Iteration Breakdown", "", "Iteration", "Mutation Operator", "Patch found at generation", "All Tests Passed", "Total time taken (s)" });
        iterationDetails.add(new String[]{"1", "BER", "1", "84.32"});

        assertEquals(CSVOutput.generalDetails.size(), generalDetails.size());
        assertEquals(CSVOutput.iterationDetails.size(), iterationDetails.size());

        // NEED TO GO THROUGH THIS LINE BY LINE AND ASSERT!
        assertEquals(CSVOutput.generalDetails.toString(), generalDetails.toString());
        assertEquals(CSVOutput.iterationDetails.toString(), iterationDetails.toString());

    }
}
