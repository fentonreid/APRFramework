package Util;

import static org.junit.jupiter.api.Assertions.*;

import GP.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;

public class TestParserRunner {
    ClassLoader classLoader = Util.class.getClassLoader();

    @Test
    @DisplayName("Cannot find configuration YAML file")
    public void testMissingConfigurationFile() {

        assertThrows(IOException.class, () -> ParserRunner.main("/src/test/resources/UtilFiles/YamlFiles/Does not exist.yml"));
    }

    @Test
    @DisplayName("GP")
    public void testGP() {
        assertThrows(UnsupportedOperationException.class, () -> ParserRunner.main("src/test/resources/UtilFiles/YamlFiles/missingGP.yml"));
    }

    @Test
    @DisplayName("Defects4J")
    public void testDefects4J() {
        assertThrows(UnsupportedOperationException.class, () -> ParserRunner.main("src/test/resources/UtilFiles/YamlFiles/missingDefects4J.yml"));
    }

    @Test
    @DisplayName("Output")
    public void testOutput() {
        assertThrows(UnsupportedOperationException.class, () -> ParserRunner.main("src/test/resources/UtilFiles/YamlFiles/missingOutput.yml"));
    }
}