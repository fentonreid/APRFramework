package Util;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestShellProcessBuilder {
    @Test
    @DisplayName("Bash")
    public void TestShellProcessBash() throws Exception {
        // Check the working directory is in the Defects4J perl script directory
        ArrayList<String> testResults = ShellProcessBuilder.getStandardInput(new String[]{"pwd"});
        assertEquals(testResults.size(), 1);
        assertEquals(testResults.get(0), "/defects4j/framework/bin");
    }

    @Test
    @DisplayName("Defects4J")
    public void TestShellProcessDefects4J() throws Exception {
        // Ensure we can check out a Defects4J bug
        Path checkoutPath = Paths.get("/APRFramework/src/test/resources/Checkout/Lang_1_test");
        ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", "Lang", "-v", 1 + "b", "-w", checkoutPath.toString()}).waitFor();
        assertTrue(checkoutPath.toFile().exists());

        // Ensure we can compile and test a Defects4J bug
        ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "compile", "-w", checkoutPath.toString()}).waitFor();
        ArrayList<String> testResults = ShellProcessBuilder.getStandardInput(new String[]{"perl", "defects4j", "test", "-r", "-w", checkoutPath.toString()});
        assertTrue(testResults.size() >= 1);
        assertTrue(testResults.get(0).contains("Failing tests"));

        // Remove checkoutPath
        FileUtils.deleteDirectory(checkoutPath.toFile());
    }
}