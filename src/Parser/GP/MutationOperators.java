package Parser.GP;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MutationOperators {
    private Object mutationOperatorObject;
    private final String classPathPrefix = "GP.MutationOperators.";

    public MutationOperators(Object mutationOperatorObject) {
        this.mutationOperatorObject = mutationOperatorObject;
    }

    public Object getMutationOperatorObject() { return mutationOperatorObject; }
    public void setMutationOperatorObject(Object mutationOperatorObject) { this.mutationOperatorObject = mutationOperatorObject; }

    // This function will determine if the mutation operators object has the correct syntax required, either:
    // mutationOperators = {mutationOperator1, mutationOperator2, mutationOperator3}
    // or
    // mutationOperators = {all mutation operators that implement xyz}
    public ArrayList<String> parseObjectStructure() throws Exception {
        // Check type of mutationOperatorObject
        if (mutationOperatorObject.getClass() == ArrayList.class) {
            ArrayList<String> mutationOperators = (ArrayList<String>) mutationOperatorObject;

            // Check that these extend mutationOperator class
            if (validateAllMutationOperators(mutationOperators)) {
                return mutationOperators;
            }

            throw new Exception("Mutation Operators is not valid, refer to the documentation and ensure mutationOperator names match");

        } else if (mutationOperatorObject.getClass() == String.class) {
            String mutationOperatorOption = mutationOperatorObject.toString().toLowerCase().strip();

            // Further, check that the mutationOperators is set to "all"
            if(mutationOperatorOption.equals("all")) {
                // Get all the mutationOperators that extend the MutationOperator class
                return getAllMutationOperatorChildren();
            }

            throw new Exception("mutationOperators does not have a valid string flag, use 'all' or specify a list of mutation operators");
        }

        throw new Exception("mutationOperators is not of type ArrayList or String and belongs to class: " + mutationOperatorObject.getClass() + " instead");
    }

    /**
     * Get all classes that implement the MutationOperator abstract class
     * @return An Arraylist of strings that contain the class names of all subclasses of the MutationOperator abstract class
     */
    public ArrayList<String> getAllMutationOperatorChildren() throws ClassNotFoundException {
        ArrayList<String> mutationOperators = new ArrayList<>();

        // Get package name and convert to directory -> GP/MutationOperators
        String packageName =  Class.forName(classPathPrefix + "AbstractMutationOperator").getPackageName().replaceAll("\\.", "/");

        // Get all files in the src/GP/MutationsOperators directory
        File mutationOperatorsPath = new File(Paths.get("src/" + packageName).toAbsolutePath().toString());
        File[] files = mutationOperatorsPath.listFiles();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();

                if(fileName.equals("AbstractMutationOperator.java") ||!fileName.endsWith(".java")) {
                    continue;
                }

                String mutationOperator = fileName.substring(0, fileName.lastIndexOf('.'));

                // Remove.java extension
                Class c = Class.forName(classPathPrefix + mutationOperator);

                if(!c.getSuperclass().getSimpleName().equals("AbstractMutationOperator")) {
                    throw new ClassNotFoundException(mutationOperator + " is not a subclass of AbstractMutationOperator.class");
                }

                mutationOperators.add(mutationOperator);
            }
        }

        return mutationOperators;
    }

    /**
     * Given an Arraylist of mutation operators class names check to ensure that they implement the MutationOperator abstract class
     * @param mutationOperators Arraylist of mutation operator class names parsed from config.yml
     * @return Whether all elements in the Arraylist of mutation operators implements the MutationOperator abstract class
     * @throws ClassNotFoundException Throws if the given mutation operator class does not exist or does not implement MutationOperator abstract class
    **/
    public boolean validateAllMutationOperators(ArrayList<String> mutationOperators) throws ClassNotFoundException {
        for (String mutationOperator : mutationOperators) {
            Class c = Class.forName(classPathPrefix + mutationOperator);

            // If the superclass is not the abstract MutationOperator class
            if(!c.getSuperclass().getSimpleName().equals("AbstractMutationOperator")) {
                throw new ClassNotFoundException(mutationOperator + " is not a subclass of AbstractMutationOperator.class");
            }
        }

        return true;
    }
}
