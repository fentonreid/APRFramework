import Output.Full.JavaProjectSkeleton;
import Output.Full.ProjectPaths;
import Parser.Parser;
import Parser.Runner;

import java.nio.file.Path;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        // Parse config.yaml
        Parser parsedObjects = new Runner(args).main();

        // Setup project paths
        ProjectPaths projectPaths = new ProjectPaths(parsedObjects);

        // Create tmp directory with correct structures based on the passed in config.yaml
        JavaProjectSkeleton.setProjectPaths(projectPaths);
        JavaProjectSkeleton.main();
        
        System.out.println("");


        // Run GP on selected Defects4J active bugs
        // ...

        // Convert output to jar

        // Create a temporary folder that should be deleted after the jar is created...

    }
}
