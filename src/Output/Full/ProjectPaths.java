package Output.Full;

import Parser.Parser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public final class ProjectPaths {
    private final Path rootPath = Paths.get("tmp/src");

    private Path configurationYAMLPath;
    private ArrayList<Path> directories;
    private List<Path> identifierBIDPaths;
    private List<Path> iterationsPaths;
    private List<Path> mutationOperatorPaths;

    public ProjectPaths(Parser parsedObjects) {
        Map<String, HashSet<Integer>> identifierToIdMap = new HashMap<>(parsedObjects.getDefects4J().getTestCaseSelection().getIdentifierToIdMap());

        ArrayList<Path> directories = new ArrayList<>();
        List<Path> identifierBIDPaths = new ArrayList<>();
        List<Path> iterationsPaths = new ArrayList<>();
        List<Path> mutationOperatorPaths = new ArrayList<>();

        // [identifier]/[BID_id]/
        for (String identifier : identifierToIdMap.keySet()) {
            identifierBIDPaths.addAll(identifierToIdMap.get(identifier).stream().map(id -> Paths.get(getRootPath().toString(), identifier, "BID_" + id.toString())).toList());
        }

        // [identifier]/[BID_id]/Iterations/[iterations]
        int iterationNumber = 1;
        for (int iteration : IntStream.rangeClosed(1, iterationNumber).toArray()) { // Will need to fix this again
            iterationsPaths.addAll(identifierBIDPaths.stream().map(path -> Paths.get(path.toString(), "Iterations", "_" + iteration)).toList());
        }

        // [identifier]/[BID_id]/Iterations/[iterations]/[mutationOperators]
        for (String mutationOperator : (ArrayList<String>) parsedObjects.getGp().getMutationOperators()) {
            mutationOperatorPaths.addAll(iterationsPaths.stream().map(path -> Paths.get(path.toString(), mutationOperator)).toList());
        }

        directories.addAll(identifierBIDPaths);
        directories.addAll(iterationsPaths);
        directories.addAll(mutationOperatorPaths);

        setConfigurationYAMLPath(Paths.get(parsedObjects.getOutput().getConfigYAMLName()));
        setIdentifierBIDPaths(identifierBIDPaths);
        setIterationsPaths(iterationsPaths);
        setMutationOperatorPaths(mutationOperatorPaths);
        setDirectories(directories);
    }

    public Path getConfigurationYAMLPath() { return configurationYAMLPath; }
    public void setConfigurationYAMLPath(Path configurationYAMLPath) { this.configurationYAMLPath = configurationYAMLPath; }

    public ArrayList<Path> getDirectories() { return directories; }
    public void setDirectories(ArrayList<Path> directories) {
        this.directories = directories;
    }

    public List<Path> getIdentifierBIDPaths() { return identifierBIDPaths; }
    public void setIdentifierBIDPaths(List<Path> identifierBIDPaths) { this.identifierBIDPaths = identifierBIDPaths; }

    public List<Path> getIterationsPaths() { return iterationsPaths; }
    public void setIterationsPaths(List<Path> iterationsPaths) { this.iterationsPaths = iterationsPaths; }

    public List<Path> getMutationOperatorPaths() { return mutationOperatorPaths; }
    public void setMutationOperatorPaths(List<Path> mutationOperatorPaths) { this.mutationOperatorPaths = mutationOperatorPaths; }

    public Path getRootPath() { return rootPath; }

    public static final class PathVariables {
        private String identifier;
        private String bid;
        private String iteration;
        private String mutationOperator;

        public PathVariables() {}

        public void setVariables(Path path) {
            ArrayList<String> variables = new ArrayList<>(List.of(path.toString().split(Pattern.quote(File.separator))));
            variables.remove("tmp");
            variables.remove("src");
            variables.remove("iterations");

            // Fill arraylist with null values if not fully occupied
            while (variables.size() < 4) { variables.add(null); }

            setIdentifier(variables.get(0));
            setBid(variables.get(1));
            setIteration(variables.get(2));
            setMutationOperator(variables.get(3));
        }

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }

        public String getBid() { return bid; }
        public void setBid(String bid) { this.bid = bid; }

        public String getMutationOperator() { return mutationOperator; }
        public void setMutationOperator(String mutationOperator) { this.mutationOperator = mutationOperator; }

        public String getIteration() { return iteration; }
        public void setIteration(String iteration) { this.iteration = iteration; }
    }
}