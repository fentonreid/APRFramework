package Output.Full;

import Parser.Parser;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

public final class ProjectPaths {
    private final Path rootPath = Paths.get("tmp/src");
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
        for (int iteration : IntStream.rangeClosed(1, 10).toArray()) { // Will need to fix this again
            iterationsPaths.addAll(identifierBIDPaths.stream().map(path -> Paths.get(path.toString(), "Iterations", "_" + iteration)).toList());
        }

        // [identifier]/[BID_id]/Iterations/[iterations]/[mutationOperators]
        for (String mutationOperator : (ArrayList<String>) parsedObjects.getGp().getMutationOperators()) {
            mutationOperatorPaths.addAll(iterationsPaths.stream().map(path -> Paths.get(path.toString(), mutationOperator)).toList());
        }

        directories.addAll(identifierBIDPaths);
        directories.addAll(iterationsPaths);
        directories.addAll(mutationOperatorPaths);

        setIdentifierBIDPaths(identifierBIDPaths);
        setIterationsPaths(iterationsPaths);
        setMutationOperatorPaths(mutationOperatorPaths);
        setDirectories(directories);
    }

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
}
