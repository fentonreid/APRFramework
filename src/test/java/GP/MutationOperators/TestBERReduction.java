package GP.MutationOperators;

import GP.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBERReduction {
    String prePath = "MutationOperatorFiles/BERReduction/Pre/";
    String postPath = "MutationOperatorFiles/BERReduction/Post/";

    @Test
    @DisplayName("BERReduction1")
    public void testBERReduction1() throws Exception {
        Util.mutationOperator = BERReduction.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERReduction1"));
    }

    @Test
    @DisplayName("BERReduction2")
    public void BERReduction2() throws Exception {
        Util.mutationOperator = BERReduction.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERReduction2"));
    }

    @Test
    @DisplayName("BERReduction3")
    public void testBERReduction3() throws Exception {
        Util.mutationOperator = BERReduction.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERReduction3"));
    }

    @Test
    @DisplayName("BERReduction4")
    public void testBERReduction4() throws Exception {
        Util.mutationOperator = BERReduction.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERReduction4"));
    }

    @Test
    @DisplayName("BERReduction5")
    public void testBERReduction5() throws Exception {
        Util.mutationOperator = BERReduction.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BERReduction5"));
    }
}