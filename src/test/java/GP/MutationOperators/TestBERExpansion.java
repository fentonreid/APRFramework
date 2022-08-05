package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import GP.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBERExpansion {
    String prePath = "MutationOperatorFiles/BERExpansion/Pre/";
    String postPath = "MutationOperatorFiles/BERExpansion/Post/";

    @Test
    @DisplayName("BERExpansion1")
    public void testBERExpansion1() throws Exception {
        Util.mutationOperator = BERExpansion.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion1"));
    }

    @Test
    @DisplayName("BERExpansion2")
    public void BERExpansion2() throws Exception {
        Util.mutationOperator = BERExpansion.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion2"));
    }

    @Test
    @DisplayName("BERExpansion3")
    public void testBERExpansion3() throws Exception {
        Util.mutationOperator = BERExpansion.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion3"));
    }

    @Test
    @DisplayName("BERExpansion4")
    public void testBERExpansion4() throws Exception {
        Util.mutationOperator = BERExpansion.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion4"));
    }

    @Test
    @DisplayName("BERExpansion5")
    public void testBERExpansion5() {
        Util.mutationOperator = BERExpansion.class;
        assertThrows(UnmodifiedProgramException.class, () -> Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion5"));
    }

    @Test
    @DisplayName("BERExpansion6")
    public void testBERExpansion6() {
        Util.mutationOperator = BERExpansion.class;
        assertThrows(UnmodifiedProgramException.class, () -> Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion6"));
    }

    @Test
    @DisplayName("BERExpansion7")
    public void testBERExpansion7() throws Exception {
        Util.mutationOperator = BERExpansion.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion7"));
    }

    @Test
    @DisplayName("BERExpansion8")
    public void testBERExpansion8() throws Exception {
        Util.mutationOperator = BERExpansion.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERExpansion8"));
    }
}