package com;

import com.Output.Full.JavaProjectSkeleton;
import com.Output.Full.ProjectPaths;
import com.Parser.Parser;
import com.Parser.Runner;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        // Parse config.yaml
        Parser parsedObjects = new Runner().main();

        // Setup project paths
        ProjectPaths projectPaths = new ProjectPaths(parsedObjects);

        // Create tmp directory with correct structures based on the passed in config.yaml
        JavaProjectSkeleton.setProjectPaths(projectPaths);
        JavaProjectSkeleton.main();

        System.out.println("");

        // Run com.GP on selected com.Defects4J active bugs
        // ...

        // Convert output to jar

        // Create a temporary folder that should be deleted after the jar is created...

    }
}
