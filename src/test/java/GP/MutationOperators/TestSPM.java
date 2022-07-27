package GP.MutationOperators;

import GP.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSPM {
    String prePath = "MutationOperatorFiles/SPM/Pre/";
    String postPath = "MutationOperatorFiles/SPM/Post/";

    @Test
    @DisplayName("SPM1")
    public void testSPM1() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "SPM1"));
    }

    @Test
    @DisplayName("SPM2")
    public void testSPM2() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "SPM2"));
    }

    @Test
    @DisplayName("SPM3")
    public void testSPM3() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "SPM3"));
    }

    @Test
    @DisplayName("SPM4")
    public void testSPM4() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "SPM4"));
    }

    @Test
    @DisplayName("SPM5")
    public void testSPM5() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "SPM5"));
    }

    @Test
    @DisplayName("SPM6")
    public void testSPM6() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "SPM6"));
    }

    @Test
    @DisplayName("SPM7")
    public void testSPM7() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "SPM7"));
    }
}