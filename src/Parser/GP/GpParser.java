package Parser.GP;

import java.util.List;

public class GpParser {
    private int generations;
    private int populationSize;
    private float crossoverRate;
    private float mutationRate;
    private Object mutationOperators;

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
}
