package com.GP.GP;

import java.util.ArrayList;

public class GP {
    int initialPopulationSize;
    double mutationRate;
    double crossoverRate;

    ArrayList<String> population = new ArrayList<>();
    ArrayList<String> newPopulation = new ArrayList<>();

    // A character set to define all possible characters that will be in created strings
    Character[] characterSet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', ' '
    };

    // Constructor to define the simpleGeneticAlgorithm
    public GP(int initialPopulationSize, double crossoverRate, double mutationRate) {
        this.initialPopulationSize = initialPopulationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    // Constructor to define the simpleGeneticAlgorithm that is used in the randomSearchAlgorithm class
    public GP(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    public void initialisePopulation() {
        // Population ArrayList with a size of the population is filled with random strings with a length of 17
        for(int currentItem=0; currentItem<this.initialPopulationSize; currentItem++) {
            StringBuilder randomSolution = new StringBuilder();
            // Generate a string of length 17
            for(int currentChar=0; currentChar<17; currentChar++) {
                randomSolution.append(characterSet[(int) Math.floor(Math.random() * characterSet.length)]);
            }

            // Add to the population
            population.add(randomSolution.toString());
        }
    }

    public int calculateFitness(String individual) {
        // The fitness function returns the number of correctly placed characters in a given string in comparison to the required solution
        int correct = 0;
        String solution = "Welcome to CS547!";

        for (int i=0; i<individual.length(); i++) {
            if (individual.charAt(i) == solution.charAt(i)) {
                correct++;
            }
        }
        return correct;
    }

    public String getParent() {
        // This function compares two random individuals in the population and returns the parent with the highest fitness
        String individualOne = population.get((int) Math.floor(Math.random() * population.size()));
        String individualTwo = population.get((int) Math.floor(Math.random() * population.size()));


        // Comparing fitness of individual one and two
        int i1 = calculateFitness(individualOne);
        int i2 = calculateFitness(individualTwo);

        if (i1 > i2) {
            return individualOne;
        } else {
            return individualTwo;
        }
    }

    public void combineParents() {
        // This function determines if crossover occurs between two parents
        String i1 = getParent();
        String i2 = getParent();

        // Apply crossover with 75% chance
        if (Math.random() <= this.crossoverRate) {
            String[] crossoverResult = crossover(i1, i2);
            i1 = crossoverResult[0];
            i2 = crossoverResult[1];
        }

        // Add both parents to the new population
        newPopulation.add(i1);
        newPopulation.add(i2);
    }

    public String[] crossover(String i1, String i2) {
        // This crossover will slice half or lower between the two strings.
        int slicingPoint = (int) Math.floor(Math.random() * 9);
        return new String[]{i1.substring(0, slicingPoint) + i2.substring(slicingPoint), i2.substring(0, slicingPoint) + i1.substring(slicingPoint)};
    }


    public String mutate(String i1) {
        // This function randomly selects a single character in the string and replaces it with a different character
        String[] randomCharArray = i1.split("");
        int randomIndex = (int) Math.floor(Math.random() * randomCharArray.length);

        randomCharArray[randomIndex] = String.valueOf(characterSet[(int) Math.floor(Math.random() * characterSet.length)]);

        return String.join("", randomCharArray);
    }

    public static void main(String[] args) {
        // New simpleGeneticAlgorithm object
        GP ga = new GP(400, 0.75, 0.05);
        ga.initialisePopulation();

        int iteration = 0;
        // While the solution has not been found in the current population
        while (!ga.population.contains("Welcome to CS547!")) {
            ga.newPopulation = new ArrayList<>();

            // Generate the new population (populationSize/2) as pairs are added to the new population for each combineParents() call
            for (int i = 0; i < ga.initialPopulationSize/2; i++) {
                ga.combineParents();
            }

            // Replace the old population with the new one
            ga.population = ga.newPopulation;

            // For all individuals in the population determine if mutation occurs or not
            for (int i = 0; i < ga.initialPopulationSize; i++) {
                if(Math.random() <= ga.mutationRate) {
                    // Replace the old individual with the new mutated individual
                    ga.population.set(i, ga.mutate(ga.population.get(i)));
                }
            }

            System.out.println("ITERATION: " + iteration + " " + ga.population);

            iteration++;
        }

        System.out.println("\nWelcome to CS547! was formed on iteration " + (iteration-1));
    }
}