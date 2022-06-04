package main.java.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

public final class ProjectPaths {
    public static ProjectPath root;
    public static ProjectPath src;
    public static ArrayList<ProjectPath> identifiers = new ArrayList<>();
    public static ArrayList<ProjectPath> bids = new ArrayList<>();
    public static ArrayList<ProjectPath> iterations = new ArrayList<>();
    public static ArrayList<ProjectPath> iteration = new ArrayList<>();
    public static ArrayList<ProjectPath> mutation = new ArrayList<>();
    public static ArrayList<ProjectPath> buggy = new ArrayList<>();
    public static ArrayList<ProjectPath> fixed = new ArrayList<>();

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

                for (String mutationOperator : mutationOperatorsFromConfig) {
                    for (int iterationCount : IntStream.rangeClosed(1, iterationFromConfig).toArray()) {
                        // Set /tmp/src/[identifiers]/BID_[bids]/Iterations/_[iterations] path
                        String iterationString = "_" + iterationCount;
                        iteration.add(new ProjectPath(Paths.get(src + "/" + identifier + "/" + bid + "/" + "Iterations" + "/" + iterationString), identifier, String.valueOf(id), String.valueOf(iterationCount)));

                        // Set /tmp/src/[identifiers]/BID_[bids]/Iterations/_[iterations]/[mutations] path
                        mutation.add(new ProjectPath(Paths.get(src + "/" + identifier + "/" + bid + "/" + "Iterations" + "/" + iterationString + "/" + mutationOperator), identifier, String.valueOf(id), String.valueOf(iterationCount), mutationOperator));
                    }
                }
            }
        }
    }

    public static void copyFileToDestination(Path target, Path destination) throws Exception {
        try {
            Files.copy(target, destination);
        } catch (IOException ex) {
            throw new Exception("Could not copy '" + target.toAbsolutePath() + "' to '" + destination.toAbsolutePath() + "'");
        }
    }

    public static void createDirectory(Path directoryToCreate) throws Exception {
        boolean created = directoryToCreate.toFile().mkdirs();

        //if(!created) {
        //    throw new Exception("Path: " + directoryToCreate + " could not be created");
        //}
    }

    public static void createDirectories(ArrayList<Path> directoriesToCreate) throws Exception {
        for (Path directoryToCreate : directoriesToCreate) {
            boolean created = directoryToCreate.toFile().mkdirs();

            //if(!created) {
            //    throw new Exception("Path: " + directoryToCreate + " could not be created");
            //}
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

            String[] variableNames = new String[]{"identifier", "bid", "iteration", "mutation"};
            for (int i=0; i<args.length; i++) {
                dynamicVariables.put(variableNames[i], args[i]);
            }
        }
    }
}