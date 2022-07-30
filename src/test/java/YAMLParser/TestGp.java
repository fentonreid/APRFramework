package YAMLParser;

import static org.junit.jupiter.api.Assertions.*;

import GP.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class TestGp {

    ClassLoader classLoader = Util.class.getClassLoader();

    public LinkedHashMap<String, Object> gpHashMap;

    public ArrayList<String> getAllMutationOperators() throws Exception {
        ArrayList<String> mutationOperators = new ArrayList<>();

        String mutationOperatorDirectory = "src/main/java/GP/MutationOperators";
        File[] mutationOperatorClasses = new File(Paths.get(mutationOperatorDirectory).toAbsolutePath().toString()).listFiles(f -> f.getName().endsWith("java"));

        if (mutationOperatorClasses == null) { throw new Exception("No mutation operators could be found in the project"); }

        for (File file : mutationOperatorClasses) {
            String fileName = file.getName();
            String mutationOperatorName = fileName.substring(0, fileName.lastIndexOf('.'));
            mutationOperators.add(mutationOperatorName);
        }

        return mutationOperators;
    }

    public void reinitialise() {
        gpHashMap = new LinkedHashMap<>();
        gpHashMap.put("generations", 1);
        gpHashMap.put("populationSize", 1);
        gpHashMap.put("mutationRate", 0.95);
        gpHashMap.put("mutationOperators", "all");
        gpHashMap.put("iterationsPerBug", 1);
        gpHashMap.put("numberOfThreads", 1);
    }

    @Test
    @DisplayName("Generations")
    public void testGenerations() throws Exception {
        reinitialise();
        assertEquals(new Gp(gpHashMap).generations, 1); // Assert property was assigned

        gpHashMap.remove("generations"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("generations", "1"); // Throws property not of type integer
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("generations", 0); // Throws not in range 1 to 1000
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("generations", 1001); // Throws not in range 1 to 1000
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("generations", 1);
        assertDoesNotThrow(() -> new Gp(gpHashMap));

        gpHashMap.put("generations", 1000);
        assertDoesNotThrow(() -> new Gp(gpHashMap));
    }

    @Test
    @DisplayName("Population Size")
    public void testPopulationSize() throws Exception {
        reinitialise();
        assertEquals(new Gp(gpHashMap).populationSize, 1); // Assert property was assigned

        gpHashMap.remove("populationSize"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", "1"); // Throws property not of type integer
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", 0); // Throws not in range 1 to 1000
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", 1001); // Throws not in range 1 to 1000
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", 1);
        assertDoesNotThrow(() -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", 1000);
        assertDoesNotThrow(() -> new Gp(gpHashMap));
    }

    @Test
    @DisplayName("Mutation Rate")
    public void testMutationRate() throws Exception {
        reinitialise();
        assertEquals(new Gp(gpHashMap).mutationRate, 0.95); // Assert property was assigned

        gpHashMap.remove("mutationRate"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("mutationRate", "0.95"); // Throws property not of type double or integer
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("mutationRate", -0.01); // Throws not in range 0 to 1
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("mutationRate", 1.01); // Throws not in range 0 to 1
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("mutationRate", 0);
        assertDoesNotThrow(() -> new Gp(gpHashMap));

        gpHashMap.put("mutationRate", 1);
        assertDoesNotThrow(() -> new Gp(gpHashMap));
    }

    @Test
    @DisplayName("Mutation Operators")
    public void testMutationOperator() throws Exception {
        ArrayList<String> mutationOperators = new ArrayList<>(getAllMutationOperators());
        assertEquals(new Gp(gpHashMap).mutationOperators, mutationOperators); // Assert property was assigned

        gpHashMap.remove("mutationOperators"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("mutationOperators", 10); // Throws property not of type String or ArrayList
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        String fakeMutationOperator = "MUTATION OPERATOR DOES NOT EXIST";
        gpHashMap.put("mutationOperators", fakeMutationOperator); // Mutation Operator not found
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        ArrayList<String> tempMutationOperator = new ArrayList<>();
        tempMutationOperator.add(mutationOperators.get(0));
        gpHashMap.put("mutationOperators", tempMutationOperator);
        assertEquals(new Gp(gpHashMap).mutationOperators.get(0), tempMutationOperator.get(0));

        gpHashMap.put("mutationOperators", "all");
        assertEquals(new Gp(gpHashMap).mutationOperators, mutationOperators);




        Path destination = Paths.get("src/main/java/GP/MutationOperators/MutationOperatorWithoutMutateMethod.java");
        Files.copy(new File(Objects.requireNonNull(classLoader.getResource( "YAMLParserFiles/MutationOperatorWithoutMutateMethod.java")).getFile()).toPath(), destination);
        gpHashMap.put("mutationOperators", "MutationOperatorWithoutMutateMethod");
        assertThrows(Exception.class, () -> new Gp(gpHashMap));
        Files.delete(destination);
    }

    @Test
    @DisplayName("Iterations Per Bug")
    public void testIterationsPerBug() throws Exception {
        reinitialise();
        assertEquals(new Gp(gpHashMap).iterationsPerBug, 1); // Assert property was assigned

        gpHashMap.remove("iterationsPerBug"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("iterationsPerBug", "1"); // Throws property not of integer
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("iterationsPerBug", 0); // Throws not in range 1 to 20
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("iterationsPerBug", 21); // Throws not in range 1 to 20
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("iterationsPerBug", 1);
        assertDoesNotThrow(() -> new Gp(gpHashMap));

        gpHashMap.put("iterationsPerBug", 20);
        assertDoesNotThrow(() -> new Gp(gpHashMap));
    }

    @Test
    @DisplayName("Number Of Threads")
    public void testNumberOfThreads() throws Exception {
        reinitialise();
        assertEquals(new Gp(gpHashMap).numberOfThreads, 1); // Assert property was assigned

        gpHashMap.remove("numberOfThreads"); // Throws property does not exist
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("numberOfThreads", "1"); // Throws property not of integer
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("numberOfThreads", 0); // Throws not in range 1 to 36
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("numberOfThreads", 37); // Throws not in range 1 to 36
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("numberOfThreads", 1);
        assertDoesNotThrow(() -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", 36);
        gpHashMap.put("numberOfThreads", 36);
        assertDoesNotThrow(() -> new Gp(gpHashMap));
    }

    @Test
    @DisplayName("Population Size and Number Of Threads constraints")
    public void testPopulationSizeAndNumberOfThreadsConstraints() {
        reinitialise();

        gpHashMap.put("populationSize", 5);
        gpHashMap.put("numberOfThreads", 20);
        assertThrows(Exception.class, () -> new Gp(gpHashMap)); // Throws population size is smaller than number of threads

        gpHashMap.put("populationSize", 8);
        gpHashMap.put("numberOfThreads", 7); // Throws populationSize not divisible by numberOfThreads
        assertThrows(Exception.class, () -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", 36);
        gpHashMap.put("numberOfThreads", 36);
        assertDoesNotThrow(() -> new Gp(gpHashMap));

        gpHashMap.put("populationSize", 4);
        gpHashMap.put("numberOfThreads", 2);
        assertDoesNotThrow(() -> new Gp(gpHashMap));
    }
}