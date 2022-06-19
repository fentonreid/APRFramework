package test.java.YAMLParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;
import main.java.YAMLParser.Output;

import static org.junit.jupiter.api.Assertions.*;

public class TestOutput {
    public LinkedHashMap<String, Object> outputHashMap;

    @BeforeEach
    public void reinitialise() {
        outputHashMap = new LinkedHashMap<>();
        outputHashMap.put("csv", "summary");
    }

    @Test
    @DisplayName("CSV")
    public void testCsv() throws Exception {
        assertEquals(new Output(outputHashMap).summaryCSV, "summary"); // Assert property was assigned

        outputHashMap.remove("csv"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Output(outputHashMap));

        outputHashMap.put("csv", 10); // Throws is not of type String
        assertThrows(Exception.class, () -> new Output(outputHashMap));
    }
}