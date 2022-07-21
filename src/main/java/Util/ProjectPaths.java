package Util;

import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Properties;

public final class ProjectPaths {
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
        catch (IOException ex) { throw new Exception("Could not copy '" + fileToCopy + "' to '" + destination + " '" + ex); }
    }
}
