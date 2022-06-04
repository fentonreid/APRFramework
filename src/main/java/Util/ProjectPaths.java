package main.java.Util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

public final class ProjectPaths {
    public ProjectPath root;
    public ProjectPath src;
    public ArrayList<ProjectPath> identifiers = new ArrayList<>();
    public ArrayList<ProjectPath> bids = new ArrayList<>();
    public ArrayList<ProjectPath> iterations = new ArrayList<>();
    public ArrayList<ProjectPath> iteration = new ArrayList<>();
    public ArrayList<ProjectPath> mutation = new ArrayList<>();

    public ProjectPaths(Map<String, HashSet<Integer>> identifiersToBugIds, int iterationFromConfig, ArrayList<String> mutationOperatorsFromConfig) {
        root = new ProjectPath(Paths.get("/tmp"));
        src = new ProjectPath(Paths.get(root + "/src"));

        // Set /tmp/src/[identifiers] path
        for (String identifier : identifiersToBugIds.keySet()) {
            identifiers.add(new ProjectPath(Paths.get(src + "/" + identifier), identifier));

            // Set /tmp/src/[identifiers]/BID_[bids] path
            for (int id : identifiersToBugIds.get(identifier)) {
                String bid = "BID_" + id;
                bids.add(new ProjectPath(Paths.get(src + "/" + identifier + "/" + bid), identifier, String.valueOf(id)));

                // Set /tmp/src/[identifiers]/BID_[bids]/Iterations path
                iterations.add(new ProjectPath(Paths.get(src + "/" + identifier + "/" + bid + "/" + "Iterations"), identifier, String.valueOf(id)));

                // Set /tmp/src/[identifiers]/BID_[bids]/Iterations/_[iterations] path
                for (int iterationCount : IntStream.rangeClosed(1, iterationFromConfig).toArray()) {
                    String iterationString = "_" + iterationCount;
                    iteration.add(new ProjectPath(Paths.get(src + "/" + identifier + "/" + bid + "/" + "Iterations" + "/" + iterationString), identifier, String.valueOf(id), String.valueOf(iterationCount)));

                    // Set /tmp/src/[identifiers]/BID_[bids]/Iterations/_[iterations]/[mutations] path
                    for (String mutationOperator : mutationOperatorsFromConfig) {
                        mutation.add(new ProjectPath(Paths.get(src + "/" + identifier + "/" + bid + "/" + "Iterations" + "/" + iterationString + "/" + mutationOperator), identifier, String.valueOf(id), String.valueOf(iterationCount), mutationOperator));
                    }
                }
            }
        }
    }

    public static class ProjectPath {
        public Path path;
        public Map<String, String> dynamicVariables = new HashMap<>();

        public String toString() {
            return path.toString();
        }

        public ProjectPath(Path path, String... args) {
            this.path = path;

            String[] variableNames = new String[]{"identifier", "bid", "iteration", "iterations"};
            for (int i=0; i<args.length; i++) {
                dynamicVariables.put(variableNames[i], args[i]);
            }
        }
    }
}