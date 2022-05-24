package Output.Full;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class JavaProjectSkeleton {
    private static final Path rootPath = Paths.get("tmp/src");
    private static ProjectPaths projectPaths;

    private JavaProjectSkeleton() { }

    public static Path getRootPath() { return rootPath; }

    public ProjectPaths getProjectPaths() { return projectPaths; }
    public static void setProjectPaths(ProjectPaths projectPaths) { JavaProjectSkeleton.projectPaths = projectPaths; }

    public static void createDirectory(Path directoryToCreate) throws Exception {
        boolean created = directoryToCreate.toFile().mkdirs();

        //if(!created) {
        //    throw new Exception("Path: " + directoryToCreate + " could not be created");
        //}
    }

    public static void createDirectories(ArrayList<Path> directoriesToCreate) throws Exception {
        for(Path directoryToCreate : directoriesToCreate) {
            boolean created = directoryToCreate.toFile().mkdirs();

            //if(!created) {
            //    throw new Exception("Path: " + directoryToCreate + " could not be created");
            //}
        }
    }

    public static void main() {
        try {
            // Create root path :: tmp/src
            createDirectory(getRootPath());

            // Create [identifier]/[bugId]/iterations/[iterations]/[mutationOperator] structures
            createDirectories(projectPaths.getDirectories());

            // Insert buggy implementation and fixed versions of each bug
            ////addBuggyAndFixedToDirectories(paths); // e.g. Chart/BID_1, here add root

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
