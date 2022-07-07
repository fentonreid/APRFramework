package YAMLParser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;

public class TestOutput {
    public LinkedHashMap<String, Object> outputHashMap;

    public void reinitialise() {
        outputHashMap = new LinkedHashMap<>();
        outputHashMap.put("csv", "summary");
        outputHashMap.put("javadoc", true);
        outputHashMap.put("patches", true);
    }

    @Test
    @DisplayName("CSV")
    public void testCsv() throws Exception {
        reinitialise();
        assertEquals(new Output(outputHashMap).summaryCSV, "summary"); // Assert property was assigned
        outputHashMap.remove("csv"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Output(outputHashMap));

        outputHashMap.put("csv", 10); // Throws is not of type String
        assertThrows(Exception.class, () -> new Output(outputHashMap));
    }

    @Test
    @DisplayName("Javadoc")
    public void testJavadoc() throws Exception {
        reinitialise();
        assertEquals(new Output(outputHashMap).javadoc, true); // Assert property was assigned

        outputHashMap.remove("javadoc"); // Do not throw when property does not exist
        assertDoesNotThrow(() -> new Output(outputHashMap));

        outputHashMap.put("javadoc", "true"); // Throws if not of type Boolean
        assertThrows(Exception.class, () -> new Output(outputHashMap));
    }

    @Test
    @DisplayName("Patches")
    public void testPatches() throws Exception {
        reinitialise();
        assertEquals(new Output(outputHashMap).patches, true); // Assert property was assigned

        outputHashMap.remove("patches"); // Do not throw when property does not exist
        assertDoesNotThrow(() -> new Output(outputHashMap));

        outputHashMap.put("patches", "true"); // Throws is not of type Boolean
        assertThrows(Exception.class, () -> new Output(outputHashMap));
    }
}