package Output.Full;

import Defects4J.perlInterpreter;

import java.io.IOException;
import java.nio.file.Files;
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
        for (Path directoryToCreate : directoriesToCreate) {
            boolean created = directoryToCreate.toFile().mkdirs();

            //if(!created) {
            //    throw new Exception("Path: " + directoryToCreate + " could not be created");
            //}
        }
    }

    public static void populateBuggyAndFixed(List<Path> identifierBIDPaths) throws Exception {
        // NEED TO CREATE PERL INTERPRETER that calls this command:
        // ‘perl defects4j checkout -p [projectname]-v [bugId][buggy (b) or fixed (f)] -w [output directory]’
        // From this we need:
            // - [projectname]      -> identifier (from path)
            // - [bugId]            -> bugId (from path)
            // - [b]                -> call b
            // - [f]                -> call f
            // - [output directory] -> identifierBIDPaths (need to check what the name will be)

        ProjectPaths.PathVariables pathVariables = new ProjectPaths.PathVariables();

        for (Path path : identifierBIDPaths) {
            pathVariables.setVariables(path);

            System.out.println(pathVariables.getIdentifier());
            System.out.println(pathVariables.getBid());

            if(pathVariables.getIdentifier() == null || pathVariables.getBid() == null) {
                throw new Exception("Path variables not found from given path '" + path + "'");
            }

            String id = pathVariables.getBid().replace("BID_", "");

            // Create buggy
            //ArrayList<String> createBuggy = perlInterpreter.getStandardInput(new String[]{"perl", "defects4j", "checkout", "-p", pathVariables.getIdentifier(), "-v", id + "b", "-w", path + "\\Defects4JBuggy"});
            ArrayList<String> createBuggy = perlInterpreter.getStandardInput(new String[]{"perl", "defects4j", "checkout", "-p", "Lang", "-v",  "1b", "-w", "tmp/src/Lang/BID_1/Defects4JBuggy"});



            System.out.println("test");

            // Create fixed


            // ‘perl defects4j checkout -p [projectname]-v [bugId][buggy (b) or fixed (f)] -w [output directory]’



        }



    }

    public static void copyConfigurationYAML(Path configPath) throws Exception {
        // Copy over current configYAML in this project to the template root directory
        Path destination = null;

        try {
            destination = Paths.get(getRootPath().toString(), configPath.getFileName().toString());
            Files.copy(configPath, destination);

        } catch (IOException ex) {
            throw new Exception("Could not copy '" + configPath + "' to '" + destination + "'");
        }
    }

    public static void main() {
        try {
            // Create root path :: tmp/src
            createDirectory(getRootPath());

            // Create [identifier]/[bugId]/iterations/[iterations]/[mutationOperator] structures
            createDirectories(projectPaths.getDirectories());

            // Insert buggy implementation and fixed versions of each bug
            populateBuggyAndFixed(projectPaths.getIdentifierBIDPaths());

            // Copy over config.yaml
            copyConfigurationYAML(projectPaths.getConfigurationYAMLPath());


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
