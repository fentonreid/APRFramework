package main.java.YAMLParser;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@SuppressWarnings("unchecked")
public class Gp {
    public int generations;
    public int populationSize;
    public double mutationRate;
    public ArrayList<String> mutationOperators = new ArrayList<>();
    public int iterationsPerBug;

    public Gp(LinkedHashMap<String, Object> gpHashMap) throws Exception {
        parse(gpHashMap);
    }

    public void parse(LinkedHashMap<String, Object> gpHashMap) throws Exception {
        if (!gpHashMap.containsKey("generations")) { throw new Exception("'generations' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("generations") instanceof Integer)) { throw new Exception("'generations' property must be an integer"); }

        if (!gpHashMap.containsKey("populationSize")) { throw new Exception("'populationSize' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("populationSize") instanceof Integer)) { throw new Exception("'populationSize' property must be an integer"); }

        if (!gpHashMap.containsKey("mutationRate")) { throw new Exception("'mutationRate' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("mutationRate") instanceof Double)) { throw new Exception("'mutationRate' property must be a double"); }

        if (!gpHashMap.containsKey("mutationOperators")) { throw new Exception("'mutationOperators' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("mutationOperators") instanceof String) && !(gpHashMap.get("mutationOperators") instanceof ArrayList)) { throw new Exception("'mutationOperators' property must be a String with the value of 'all' or a list of mutation operators"); }

        if (!gpHashMap.containsKey("iterationsPerBug")) { throw new Exception("'iterationsPerBug' property is missing in the config.yml 'gp' object "); }
        if (!(gpHashMap.get("iterationsPerBug") instanceof Integer)) { throw new Exception("'iterationsPerBug' property must be an integer"); }

        generations = (int) gpHashMap.get("generations");
        populationSize = (int) gpHashMap.get("populationSize");
        mutationRate = (double) gpHashMap.get("mutationRate");
        iterationsPerBug = (int) gpHashMap.get("iterationsPerBug");

        // Ensure constraints of properties are correct
        if (generations < 10 || generations > 10_000) { throw new Exception("'generations' property must be between 10 and 10,000"); }
        if (populationSize < 10 || populationSize > 10_000) { throw new Exception("'populationSize' property must be between 10 and 10,000"); }
        if (mutationRate < 0.0 || mutationRate > 1.0) { throw new Exception("'mutationRate' property must be between 0 and 1"); }
        if (gpHashMap.get("mutationOperators") instanceof String && !((String) gpHashMap.get("mutationOperators")).toLowerCase().trim().equals("all")) { throw new Exception("'mutationOperators' property is not valid as the String value does not equal 'all'"); }
        if (iterationsPerBug < 1 || iterationsPerBug > 20) { throw new Exception("'iterationsPerBug' property must be between 1 and 20"); }

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
     * Get all classes that implement the MutationOperator abstract class and set the mutationOperators field
     */
    public void getAllMutationOperators() throws Exception {
        // Get all class files in GP/MutationOperators directory
        String mutationOperatorDirectory = "src/main/java/GP/MutationOperators";
        File[] mutationOperatorClasses = new File(Paths.get(mutationOperatorDirectory).toAbsolutePath().toString()).listFiles();

        if (mutationOperatorClasses == null) { throw new Exception("No mutation operators could be found in the project"); }

        for (File file : mutationOperatorClasses) {
            String fileName = file.getName();

            if(fileName.equals("AbstractMutationOperator.java") || !fileName.endsWith(".java")) { continue; }

            String mutationOperatorName = fileName.substring(0, fileName.lastIndexOf('.'));
            Class<?> c = Class.forName("main.java.GP.MutationOperators." + mutationOperatorName);

            if(!c.getSuperclass().getSimpleName().equals("AbstractMutationOperator")) { throw new ClassNotFoundException(mutationOperatorName + " is not a subclass of AbstractMutationOperator.class"); }

            mutationOperators.add(mutationOperatorName);
        }
    }

    /**
     * Given an Arraylist of mutation operators class names check to ensure that they implement the MutationOperator abstract class
     * @param selectedOperators Arraylist of mutation operator class names parsed from config.yml
     * @throws ClassNotFoundException Throws if the given mutation operator class does not exist or does not implement MutationOperator abstract class
     **/
    public void getSelectedMutationOperators(ArrayList<String> selectedOperators) throws Exception {
        for (String mutationOperator : selectedOperators) {
            Class<?> c;
            try { c = Class.forName("main.java.GP.MutationOperators." + mutationOperator); }
            catch (ClassNotFoundException ex) { throw new Exception("Class '" + mutationOperator + "' could not be found, make sure the mutation operator exists"); }

            // If the superclass is not the abstract MutationOperator class
            if (!c.getSuperclass().getSimpleName().equals("AbstractMutationOperator")) { throw new Exception(mutationOperator + " is not a subclass of AbstractMutationOperator.class"); }

            mutationOperators.add(mutationOperator);
        }
    }
}
