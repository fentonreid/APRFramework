package YAMLParser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;

public class TestOutput {
    public LinkedHashMap<String, Object> outputHashMap;

    public void reinitialise() {
        outputHashMap = new LinkedHashMap<>();
        outputHashMap.put("csv", "summary");
        outputHashMap.put("javadoc", true);
        outputHashMap.put("uploadToPatchViewer", true);
        outputHashMap.put("uploadToGeneratePatches", true);
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
    @DisplayName("Upload to patch viewer")
    public void testPatchViewer() throws Exception {
        reinitialise();
        assertEquals(new Output(outputHashMap).uploadToPatchViewer, true); // Assert property was assigned

        outputHashMap.remove("uploadToPatchViewer"); // Do not throw when property does not exist
        assertDoesNotThrow(() -> new Output(outputHashMap));

        outputHashMap.put("uploadToPatchViewer", "true"); // Throws is not of type Boolean
        assertThrows(Exception.class, () -> new Output(outputHashMap));
    }

    @Test
    @DisplayName("Upload to generated patches")
    public void testGeneratedPatches() throws Exception {
        reinitialise();
        assertEquals(new Output(outputHashMap).uploadToGeneratePatches, true); // Assert property was assigned

        outputHashMap.remove("uploadToGeneratePatches"); // Do not throw when property does not exist
        assertDoesNotThrow(() -> new Output(outputHashMap));

        outputHashMap.put("uploadToGeneratePatches", "true"); // Throws is not of type Boolean
        assertThrows(Exception.class, () -> new Output(outputHashMap));
    }
}