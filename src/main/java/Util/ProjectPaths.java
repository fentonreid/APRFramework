package main.java.Util;

import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public final class ProjectPaths {
    public static Path getBuggyProgramPath(String checkoutPath) throws Exception {
        // Read the defects4j.build.properties file
        Properties prop;
        try {
            prop = new Properties();
            prop.load(Files.newInputStream(Paths.get(checkoutPath + "/defects4j.build.properties")));
        } catch (FileNotFoundException ex) { throw new Exception("'defects4j.build.properties' file not found suggesting '" + checkoutPath + "' was not fetched correctly"); }

        String modifiedClass = prop.getProperty("d4j.classes.modified");
        String relevantClasses = prop.getProperty("d4j.classes.relevant");
        String pathToClasses = prop.getProperty("d4j.dir.src.classes");

        // Ensure properties are not null, the modified class has a length of one, relevant bugs include the modified bug, srcPath + modifiedBug exists
        if (modifiedClass == null || relevantClasses == null || pathToClasses == null) { throw new NullPointerException("Could not read 'defects4j.build.properties' file correctly"); }
        if (modifiedClass.split(",").length > 1) { throw new IndexOutOfBoundsException("The modified class has a length greater than one and therefore is not compatible with this framework '" + modifiedClass + "' "); }
        if (!(relevantClasses.contains(modifiedClass))) { throw new NullPointerException("The relevant classes does not include the modified class and therefore the bug cannot be fixed by changing " + modifiedClass); }

        Path modifiedClassPath = Paths.get("/" + pathToClasses + "/" + modifiedClass.replaceAll("\\.", File.separator) + ".java");
        if(!Files.exists(Paths.get(checkoutPath + modifiedClassPath))) { throw new Exception("Could not find the modified class path '" + modifiedClassPath +"'"); }

        return modifiedClassPath;
    }

    public static Path getFixedProgramPath(String identifier, int bid) throws Exception {
        Path patchPath = Paths.get("/defects4j/framework/projects/" + identifier + "/patches/" + bid + ".src.patch");
        if(!Files.exists(patchPath)) { throw new IOException("Could not find the patch file at '" + patchPath + "'"); }

        return patchPath;
    }

    public static void saveBugsToFileSystem(String identifier, int bid, String mutationOperatorName, ArrayList<CompilationUnit> patches) throws Exception {
        File outputFile = new File("/output/" + identifier + "_" + bid + "/" + mutationOperatorName + "/");
        if (!outputFile.mkdirs()) { throw new FileAlreadyExistsException("Failed to create '" + outputFile + "'"); }

        int patchesSize = patches.size();
        for (int i=0; i<patchesSize; i++) {
            writeToFile(Paths.get(outputFile + "/" + (i+1)), patches.get(i).toString());
        }
    }

    public static void writeToFile(Path writePath, String stringContents) throws Exception {
        try { Files.write(writePath, stringContents.getBytes()); }
        catch (IOException ex) { throw new Exception("Could not write to `" + writePath + "`"); }
    }

    public static void copyFile(Path fileToCopy, Path destination) throws Exception {
        try { Files.copy(fileToCopy, destination); }
        catch (IOException ex) { throw new Exception("Could not copy '" + fileToCopy + "' to '" + destination + " '"); }
    }
}
