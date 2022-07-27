package GP.MutationOperators;

import GP.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBEM {
    String prePath = "MutationOperatorFiles/BEM/Pre/";
    String postPath = "MutationOperatorFiles/BEM/Post/";

    @Test
    @DisplayName("BEM1")
    public void testBEM1() throws Exception {
        Util.mutationOperator = BEM.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BEM1"));
    }

    @Test
    @DisplayName("BEM2")
    public void BEM2() throws Exception {
        Util.mutationOperator = BEM.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BEM2"));
    }

    @Test
    @DisplayName("BEM3")
    public void testBEM3() throws Exception {
        Util.mutationOperator = BEM.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BEM3"));
    }

    @Test
    @DisplayName("BEM4")
    public void testBEM4() throws Exception {
        Util.mutationOperator = BEM.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BEM4"));
    }

    @Test
    @DisplayName("BEM5")
    public void testBEM5() throws Exception {
        Util.mutationOperator = BEM.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BEM5"));
    }

    @Test
    @DisplayName("BEM6")
    public void testBEM6() throws Exception {
        Util.mutationOperator = BEM.class;
        assertTrue(Util.iterationMutationUntilResolved(prePath, postPath, "BEM6"));
    }
}