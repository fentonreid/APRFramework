package main.java.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GPRunner {
    public ArrayList<ProjectPaths.ProjectPath> bidPaths;
    public ArrayList<ProjectPaths.ProjectPath> mutationPaths;

    public GPRunner() throws Exception {
        this.bidPaths = ProjectPaths.bids;
        this.mutationPaths = ProjectPaths.mutation;

        // Get the buggy path for each bug and save buggy and fixed to ProjectPaths
        for (ProjectPaths.ProjectPath path : bidPaths) {
            Path buggyPath = saveFixedAndBuggyToDirectory(path);
            System.out.println(buggyPath);
        }

        // Go to the mutation path
        for (ProjectPaths.ProjectPath path : mutationPaths) {
            System.out.println(path);
            // Take in the buggy java file and pass into AST
            // Take MutationOperator class and pass in to GP
        }
    }

    private Path saveFixedAndBuggyToDirectory(ProjectPaths.ProjectPath bidPath) throws Exception {
        String identifier = bidPath.dynamicVariables.get("identifier");
        int bid = Integer.parseInt(bidPath.dynamicVariables.get("bid"));

        String checkoutPath = "/tmp/" + identifier + "_" + bid + "_" + "buggy";
        Process finishedProcess = ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", identifier, "-v", bid + "b", "-w", checkoutPath });

        if (new InputStreamReader(finishedProcess.getInputStream()).read() != -1) { throw new Exception("Error when trying to checkout '" + identifier + "' with a bug id of '" + bid + "'"); };
        if(!Files.exists(Paths.get(checkoutPath))) { throw new Exception("Could not checkout '" + checkoutPath + "' properly"); }

        // Read the defects4j.build.properties file
        Properties prop;
        try {
            prop = new Properties();
            prop.load(new FileInputStream(checkoutPath + "/defects4j.build.properties"));
        } catch (FileNotFoundException ex) { throw new Exception("'defects4j.build.properties' file not found suggesting '" + checkoutPath + "' was not fetched correctly"); }

        String modifiedClass = prop.getProperty("d4j.classes.modified");
        String relevantClasses = prop.getProperty("d4j.classes.relevant");
        String pathToClasses = prop.getProperty("d4j.dir.src.classes");

        // Ensure properties are not null, the modified class has a length of one, relevant bugs include the modified bug, srcPath + modifiedBug exists
        if (modifiedClass == null || relevantClasses == null || pathToClasses == null) { throw new Exception("Could not read 'defects4j.build.properties' file correctly"); }
        if (modifiedClass.split(",").length > 1) { throw new Exception("The modified class has a length greater than one and therefore is not compatible with this framework '" + modifiedClass + "' "); }
        if (!(relevantClasses.contains(modifiedClass))) { throw new Exception("The relevant classes does not include the modified class and therefore the bug cannot be fixed by changing " + modifiedClass); }

        // Buggy file path
        Path modifiedClassPath = Paths.get(checkoutPath + "/" + pathToClasses + "/" + modifiedClass.replaceAll("\\.", File.separator) + ".java");
        if(!Files.exists(modifiedClassPath)) { throw new Exception("Could not find the modified class path '" + modifiedClassPath +"'"); }
        ProjectPaths.buggy.add(new ProjectPaths.ProjectPath(modifiedClassPath, identifier, String.valueOf(bid)));

        // Fixed file path
        Path patchPath = Paths.get("/APRFramework/defects4j/framework/projects/" + identifier + "/patches/" + bid + ".src.patch");
        if(!Files.exists(patchPath)) { throw new Exception("Could not find the patch file at '" + patchPath + "'"); }
        ProjectPaths.fixed.add(new ProjectPaths.ProjectPath(patchPath, identifier, String.valueOf(bid)));

        return modifiedClassPath;

        // Copy buggy and fixed patches into correct location
        //ProjectPaths.createDirectory(bidPath.path);
        //Path buggyPath = Paths.get(bidPath + "/Defects4JBuggy.java");
        //ProjectPaths.copyFileToDestination(modifiedClassPath, buggyPath);

        //Path fixedPath = Paths.get(bidPath + "/Defects4JFixed.patch");
        //ProjectPaths.copyFileToDestination(patchPath, fixedPath);

        //return new Path[] {buggyPath, fixedPath};
    }
}
