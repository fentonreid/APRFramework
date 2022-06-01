package com.Parser.GP;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.GP.MutationOperators.AbstractMutationOperator;
import com.GP.MutationOperators.MutationOperator1;
import com.GP.MutationOperators.MutationOperator2;
import com.GP.MutationOperators.MutationOperator3;

public class GpParser {
    private int generations;
    private int populationSize;
    private float crossoverRate;
    private float mutationRate;
    private Object mutationOperators;
    private final String classPathPrefix = "com.GP.MutationOperators.";


    public int getGenerations() { return generations; }
    public void setGenerations(int generations) { this.generations = generations; }

    public int getPopulationSize() { return populationSize; }
    public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }

    public float getCrossoverRate() { return crossoverRate; }
    public void setCrossoverRate(float crossoverRate) { this.crossoverRate = crossoverRate; }

    public float getMutationRate() { return mutationRate; }
    public void setMutationRate(float mutationRate) { this.mutationRate = mutationRate; }

    public Object getMutationOperators() { return mutationOperators; }
    public void setMutationOperators(Object mutationOperators) { this.mutationOperators = mutationOperators; }

    public void setup() throws Exception {
        // Check that gp properties are within bounds
        if (getGenerations() < 10 || getGenerations() > 10_000) { throw new Exception("Generation property has to be between a value of 10 and 10,000"); }
        if (getPopulationSize() < 10 || getPopulationSize() > 10_000) { throw new Exception("PopulationSize property has to be between a value of 10 and 10,000"); }
        if (getCrossoverRate() < 0.0 || getCrossoverRate() > 1.0) { throw new Exception("CrossoverRate property has to be 0 and 1"); }
        if (getMutationRate() < 0.0 || getMutationRate() > 1.0) { throw new Exception("MutationRate property has to be 0 and 1"); }
        if (getMutationOperators() == null) { throw new Exception("MutationOperators property is null"); }

        parseObjectStructure();
    }

    public void parseObjectStructure() throws Exception {
        // Check type of mutationOperatorObject
        if (getMutationOperators().getClass() == ArrayList.class) {
            ArrayList<String> mutationOperators = (ArrayList<String>) getMutationOperators();

            // Check that these extend mutationOperator class
            if (validateAllMutationOperators(mutationOperators)) {
                setMutationOperators(mutationOperators);
                return;
            }

            throw new Exception("Mutation Operators is not valid, refer to the documentation and ensure mutationOperator names match");

        } else if (getMutationOperators().getClass() == String.class) {
            String mutationOperatorOption = getMutationOperators().toString().toLowerCase().trim();

            // Further, check that the mutationOperators is set to "all"
            if(mutationOperatorOption.equals("all")) {
                // Get all the mutationOperators that extend the MutationOperator class
                setMutationOperators(getAllMutationOperatorChildren());
                return;
            }

            throw new Exception("mutationOperators does not have a valid string flag, use 'all' or specify a list of mutation operators");
        }

        throw new Exception("mutationOperators is not of type ArrayList or String and belongs to class: " + getMutationOperators().getClass() + " instead");
    }

    /**
     * Get all classes that implement the MutationOperator abstract class
     * @return An Arraylist of strings that contain the class names of all subclasses of the MutationOperator abstract class
     */
    public ArrayList<String> getAllMutationOperatorChildren() throws ClassNotFoundException {
        ArrayList<String> mutationOperators = new ArrayList<>();

        // Get package name and convert to directory -> com/GP/MutationOperators
        Class<?> test =  Class.forName(classPathPrefix + "AbstractMutationOperator");
        String packageName = test.getPackage().getName().replaceAll("\\.", "/");

        // Get all files in the src/com.GP/MutationsOperators directory
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
                Class<?> c = Class.forName(classPathPrefix + mutationOperator);

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