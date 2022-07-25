package YAMLParser;

import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * The Gp class validates the YAML GP properties.
 */
@SuppressWarnings("unchecked")
public class Gp {
    public int generations;
    public int populationSize;
    public double mutationRate;
    public ArrayList<String> mutationOperators = new ArrayList<>();
    public int iterationsPerBug;
    public int numberOfThreads;

    /**
     * Class constructor parsing the given LinkedHashMap.
     *
     * @exception Exception HashMap could not be assigned
     */
    public Gp(LinkedHashMap<String, Object> gpHashMap) throws Exception {
        parse(gpHashMap);
    }

    /**
     * Parse method ensures presence and correct instance types of YAML properties for the GP object.
     * Constraint validation of specific properties such as population size are performed.
     *
     * @param gpHashMap         GP YAML object from config
     * @exception Exception     If gpHashMap is missing a property or is of the wrong type
     */
    public void parse(LinkedHashMap<String, Object> gpHashMap) throws Exception {
        if (!gpHashMap.containsKey("generations")) { throw new Exception("'generations' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("generations") instanceof Integer)) { throw new Exception("'generations' property must be an integer"); }

        if (!gpHashMap.containsKey("populationSize")) { throw new Exception("'populationSize' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("populationSize") instanceof Integer)) { throw new Exception("'populationSize' property must be an integer"); }

        if (!gpHashMap.containsKey("mutationRate")) { throw new Exception("'mutationRate' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("mutationRate") instanceof Double) && !(gpHashMap.get("mutationRate") instanceof Integer)) { throw new Exception("'mutationRate' property must be a double"); }

        if (!gpHashMap.containsKey("mutationOperators")) { throw new Exception("'mutationOperators' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("mutationOperators") instanceof String) && !(gpHashMap.get("mutationOperators") instanceof ArrayList)) { throw new Exception("'mutationOperators' property must be a String with the value of 'all' or a list of mutation operators"); }

        if (!gpHashMap.containsKey("iterationsPerBug")) { throw new Exception("'iterationsPerBug' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("iterationsPerBug") instanceof Integer)) { throw new Exception("'iterationsPerBug' property must be an integer"); }

        if (!gpHashMap.containsKey("numberOfThreads")) { throw new Exception("'numberOfThreads' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("numberOfThreads") instanceof Integer)) { throw new Exception("'numberOfThreads' property must be an integer"); }

        generations = (int) gpHashMap.get("generations");
        populationSize = (int) gpHashMap.get("populationSize");

        if (gpHashMap.get("mutationRate") instanceof Integer) { mutationRate = (int) gpHashMap.get("mutationRate") * 1.0; }
        else { mutationRate = (Double) gpHashMap.get("mutationRate"); }

        iterationsPerBug = (int) gpHashMap.get("iterationsPerBug");
        numberOfThreads = (int) gpHashMap.get("numberOfThreads");

        // Ensure constraints of properties are correct
        if (generations < 1 || generations > 1_000) { throw new Exception("'generations' property must be between 1 and 1000"); }
        if (populationSize < 1 || populationSize > 1_000) { throw new Exception("'populationSize' property must be between 1 and 1000"); }
        if (mutationRate < 0 || mutationRate > 1) { throw new Exception("'mutationRate' property must be between 0 and 1"); }
        if (gpHashMap.get("mutationOperators") instanceof String && !((String) gpHashMap.get("mutationOperators")).toLowerCase().trim().equals("all")) { throw new Exception("'mutationOperators' property is not valid as the String value does not equal 'all'"); }
        if (iterationsPerBug < 1 || iterationsPerBug > 20) { throw new Exception("'iterationsPerBug' property must be between 1 and 20"); }
        if (numberOfThreads < 1 || numberOfThreads > 36) { throw new Exception("'numberOfThreads' property must be between 1 and 36"); }
        if (populationSize < numberOfThreads) { throw new Exception("'numberOfThreads' property must be smaller than the population size"); }
        if (populationSize % numberOfThreads != 0) { throw new Exception("'populationSize' property must be divisible by the number of threads"); }

        // Further mutation operator parsing
        if (gpHashMap.get("mutationOperators") instanceof String) {
            getAllMutationOperators();
        } else {
            for (Object t : (ArrayList<Object>) gpHashMap.get("mutationOperators")) {
                if (!(t instanceof String)) { throw new Exception("Mutation Operator '" + t + "' is not of type String"); }
            }

            getSelectedMutationOperators((ArrayList<String>) gpHashMap.get("mutationOperators"));
        }
    }

    /**
     * Get all classes that are present in the GP.MutationOperators package that implement a Mutate method.
     *
     * @exception Exception If process builder call to execute Defects4j command fails
     */
    public void getAllMutationOperators() throws Exception {
        // Get all class files in GP/MutationOperators directory
        String mutationOperatorDirectory = "src/main/java/GP/MutationOperators";
        File[] mutationOperatorClasses = new File(Paths.get(mutationOperatorDirectory).toAbsolutePath().toString()).listFiles(f -> f.getName().endsWith("java"));

        if (mutationOperatorClasses == null) { throw new Exception("No mutation operators could be found in the project"); }

        for (File file : mutationOperatorClasses) {
            String fileName = file.getName();

            String mutationOperatorName = fileName.substring(0, fileName.lastIndexOf('.'));
            Class<?> c = Class.forName("GP.MutationOperators." + mutationOperatorName);

            // Ensure the mutation operates implements the mutate method
            try { c.getMethod("mutate", CompilationUnit.class); }
            catch (NoSuchMethodException ex) { throw new Exception(mutationOperatorName + " does not implement a mutate method"); }

            mutationOperators.add(mutationOperatorName);
        }
    }

    /**
     * Given an Arraylist of mutation operators class names check to ensure that they all implement a mutate method.
     *
     * @param selectedOperators         Arraylist of mutation operator class names parsed from config.yml
     * @throws ClassNotFoundException   Throws if the given mutation operator class does not exist or does not implement a mutate method
     **/
    public void getSelectedMutationOperators(ArrayList<String> selectedOperators) throws Exception {
        for (String mutationOperator : selectedOperators) {
            Class<?> c;
            try { c = Class.forName("GP.MutationOperators." + mutationOperator); }
            catch (ClassNotFoundException ex) { throw new Exception("Class '" + mutationOperator + "' could not be found, make sure the mutation operator exists"); }

            // Ensure the mutation operates implements the mutate method
            try { c.getMethod("mutate", CompilationUnit.class); }
            catch (NoSuchMethodException ex) { throw new Exception(mutationOperator + " does not implement a mutate method"); }

            mutationOperators.add(mutationOperator);
        }
    }
}
