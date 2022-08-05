package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import GP.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class TestGNR {
    String prePath = "MutationOperatorFiles/GNR/Pre/";
    String postPath = "MutationOperatorFiles/GNR/Post/";

    @Test
    @DisplayName("GNR1")
    public void testGNR1() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR1"));
    }

    @Test
    @DisplayName("GNR2")
    public void testGNR2() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR2"));
    }

    @Test
    @DisplayName("GNR3")
    public void testGNR3() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR3"));
    }

    @Test
    @DisplayName("GNR4")
    public void testGNR4() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR4"));
    }

    @Test
    @DisplayName("GNR5")
    public void testGNR5() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR5"));
    }

    @Test
    @DisplayName("GNR6")
    public void testGNR6() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR6"));
    }

    @Test
    @DisplayName("GNR7")
    public void testGNR7() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR7"));
    }

    @Test
    @DisplayName("GNR8")
    public void testGNR8() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR8"));
    }

    @Test
    @DisplayName("GNR9")
    public void testGNR9() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR9"));
    }

    @Test
    @DisplayName("GNR10")
    public void testGNR10() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR10"));
    }

    @Test
    @DisplayName("GNR11")
    public void testGNR11() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR11"));
    }

    @Test
    @DisplayName("GNR12")
    public void testGNR12() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR12"));
    }

    @Test
    @DisplayName("GNR13")
    public void testGNR13() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR13"));
    }

    @Test
    @DisplayName("GNR14")
    public void testGNR14() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR14"));
    }

    @Test
    @DisplayName("GNR15")
    public void testGNR15() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR15"));
    }

    @Test
    @DisplayName("GNR16")
    public void testGNR16() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR16"));
    }

    @Test
    @DisplayName("GNR17")
    public void testGNR17() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR17"));
    }

    @Test
    @DisplayName("GNR18")
    public void testGNR18() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR18"));
    }

    @Test
    @DisplayName("GNR19")
    public void testGNR19() throws Exception {
        Util.mutationOperator = GNR.class;
        assertFalse(Util.iterationMutationUntilResolved(prePath, postPath, "GNR19"));
    }

    @Test
    @DisplayName("GNR20")
    public void testGNR20() throws Exception {
        Util.mutationOperator = GNR.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "GNR20"));
    }
}