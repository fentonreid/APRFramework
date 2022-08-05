package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import GP.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class TestLRRelocation {
    String prePath = "MutationOperatorFiles/LRRelocation/Pre/";
    String postPath = "MutationOperatorFiles/LRRelocation/Post/";

    @Test
    @DisplayName("LRRelocation1")
    public void testLRRelocation1() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation1"));
    }

    @Test
    @DisplayName("LRRelocation2")
    public void testLRRelocation2() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation2"));
    }

    @Test
    @DisplayName("LRRelocation3")
    public void testLRRelocation3() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation3"));
    }

    @Test
    @DisplayName("LRRelocation4")
    public void testLRRelocation4() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation4"));
    }

    @Test
    @DisplayName("LRRelocation5")
    public void testLRRelocation5() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation5"));
    }

    @Test
    @DisplayName("LRRelocation6")
    public void testLRRelocation6() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation6"));
    }

    @Test
    @DisplayName("LRRelocation7")
    public void testLRRelocation7() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation7"));
    }

    @Test
    @DisplayName("LRRelocation8")
    public void testLRRelocation8() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation8"));
    }

    @Test
    @DisplayName("LRRelocation9")
    public void testLRRelocation9() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation9"));
    }

    @Test
    @DisplayName("LRRelocation10")
    public void testLRRelocation10() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation10"));
    }

    @Test
    @DisplayName("LRRelocation11")
    public void testLRRelocation11() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation11"));
    }

    @Test
    @DisplayName("LRRelocation12")
    public void testLRRelocation12() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertFalse(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation12"));
    }

    @Test
    @DisplayName("LRRelocation13")
    public void testLRRelocation13() throws Exception {
        Util.mutationOperator = LRRelocation.class;
        assertFalse(Util.iterationMutationUntilResolved(prePath, postPath, "LRRelocation13"));
    }
}