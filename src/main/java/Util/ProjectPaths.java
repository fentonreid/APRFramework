package Util;

import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * The ProjectPaths class provides helper functions for accessing the file system of the project and fetching buggy and fixed Defects4J patches.
 */
public final class ProjectPaths {

    /**
     * Get source directory of the checked out project, this is the location on the file system where all the checked out projects source code lies.
     *
     * @param checkoutPath  The location on the file system where the checked out project lies
     * @return              The location of the checked out projects source code directory
     * @throws Exception    If the Defects4j build properties file doesn't exist or the dir.src.classes property could not be read
     */
    public static String getSourceDirectoryPath(String checkoutPath) throws Exception {
        Properties prop;
        try {
            prop = new Properties();
            prop.load(Files.newInputStream(Paths.get(checkoutPath + "/defects4j.build.properties")));
        } catch (FileNotFoundException ex) { throw new Exception("'defects4j.build.properties' file not found suggesting '" + checkoutPath + "' was not fetched correctly"); }

        String pathToClasses = prop.getProperty("d4j.dir.src.classes");

        if (pathToClasses == null) { throw new NullPointerException("Could not read 'defects4j.build.properties' file correctly"); }

        return checkoutPath + "/" + pathToClasses;
    }

    /**
     * Get path to the checked out projects, buggy program. Specifically the modified class that when mutated correctly will result in a patch.
     *
     * @param checkoutPath  The location on the file system where the checked out project lies
     * @return              The path to the checkout projects, buggy program
     * @throws Exception    If the Defects4j build properties file doesn't exist or the classes.modified and/or d4j.dir.src.classes properties are not formatted correctly
     */
    public static Path getBuggyProgramPath(String checkoutPath) throws Exception {
        Properties prop;
        try {
            prop = new Properties();
            prop.load(Files.newInputStream(Paths.get(checkoutPath + "/defects4j.build.properties")));
        } catch (FileNotFoundException ex) { throw new Exception("'defects4j.build.properties' file not found suggesting '" + checkoutPath + "' was not fetched correctly"); }

        String modifiedClass = prop.getProperty("d4j.classes.modified");
        String pathToClasses = prop.getProperty("d4j.dir.src.classes");

        // Ensure properties are not null, the modified class has a length of one, relevant bugs include the modified bug, srcPath + modifiedBug exists
        if (modifiedClass == null || pathToClasses == null) {
            throw new NullPointerException("Could not read 'defects4j.build.properties' file correctly"); }

        Path modifiedClassPath = Paths.get("/" + pathToClasses + "/" + modifiedClass.replaceAll("\\.", File.separator) + ".java");
        if(!Files.exists(Paths.get(checkoutPath + modifiedClassPath))) { throw new Exception("Could not find the modified class path '" + modifiedClassPath +"'"); }

        return modifiedClassPath;
    }

    /**
     * Gets the path to the fixed program file from the /defects4j file.
     *
     * @param identifier    Defects4j project file name e.g. Lang or Closure
     * @param bid           Unique id of the Defects4j bug
     * @return              The path to the fixed program patch file
     * @throws Exception    If the fixed project path cannot be copied
     */
    public static Path getFixedProgramPath(String identifier, int bid) throws Exception {
        Path patchPath = Paths.get("/defects4j/framework/projects/" + identifier + "/patches/" + bid + ".src.patch");
        if(!Files.exists(patchPath)) { throw new IOException("Could not find the patch file at '" + patchPath + "'"); }

        return patchPath;
    }

    /**
     * Copies a set of patches to the /output/ folder.
     *
     * @param identifier                Defects4j project file name e.g. Lang or Closure
     * @param bid                       Unique id of the Defects4j bug
     * @param mutationOperatorName      The name of the mutation operator that was used to create the patch
     * @param patches                   An ArrayList of patches that where created by the GP for a specific bug id
     * @throws Exception                If the patch cannot be copied to the /output directory
     */
    public static void saveBugsToFileSystem(String identifier, int bid, String mutationOperatorName, ArrayList<CompilationUnit> patches) throws Exception {
        File outputFile = new File("/output/" + identifier + "_" + bid + "/" + mutationOperatorName + "/");
        if (!outputFile.mkdirs()) { throw new FileAlreadyExistsException("Failed to create '" + outputFile + "'"); }

        int patchesSize = patches.size();
        for (int i=0; i<patchesSize; i++) {
            writeToFile(Paths.get(outputFile + "/" + (i+1)), patches.get(i).toString());
        }
    }

    /**
     * Copy the contents of a String to a given write path.
     *
     * @param writePath         The file name to replace with the given stringContents
     * @param stringContents    The String to replace the writePath with
     * @throws IOException      The writePath could not be written too
     */
    public static void writeToFile(Path writePath, String stringContents) throws IOException {
        try { Files.write(writePath, stringContents.getBytes()); }
        catch (IOException ex) { throw new IOException("Could not write to `" + writePath + "`"); }
    }

    /**
     * Copy a file to another location on the file system.
     *
     * @param fileToCopy        The path to the file that will be copied
     * @param destination       The destination path that the file will be copied to
     * @throws Exception        The file could not be copied
     */
    public static void copyFile(Path fileToCopy, Path destination) throws Exception {
        try { Files.copy(fileToCopy, destination); }
        catch (IOException ex) { throw new Exception("Could not copy '" + fileToCopy + "' to '" + destination + " '" + ex); }
    }
}
