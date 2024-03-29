package Util;

import static org.junit.jupiter.api.Assertions.*;

import GP.Util;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Objects;

public class TestProjectPaths {
    final Path checkoutPath = Paths.get("/APRFramework/src/test/java/Util/Lang_1_test");

    public void reinitialise() throws Exception {
        ShellProcessBuilder.runCommand(new String[]{"perl", "defects4j", "checkout", "-p", "Lang", "-v", 1 + "b", "-w", checkoutPath.toString()});
    }

    public void deleteCheckout() throws Exception {
        FileUtils.deleteDirectory(checkoutPath.toFile());
    }

    @Test
    @DisplayName("Get Buggy Program Path")
    public void testGetBuggyProgramPath() throws Exception {
        ClassLoader classLoader = Util.class.getClassLoader();
        reinitialise();

        // Valid build.properties file
        assertEquals(ProjectPaths.getBuggyProgramPath(checkoutPath.toString()).toString(),"/src/main/java/org/apache/commons/lang3/math/NumberUtils.java");

        Path buildPropertiesFile = Paths.get(checkoutPath + "/" + "defects4j.build.properties");

        // Invalid build.properties file -> Missing Modified Classes
        Files.copy(new File(Objects.requireNonNull(classLoader.getResource( "UtilFiles/BuildPropertyFiles/missingModifiedClass.properties")).getFile()).toPath(), buildPropertiesFile, StandardCopyOption.REPLACE_EXISTING);
        assertThrows(NullPointerException.class, () -> ProjectPaths.getBuggyProgramPath(checkoutPath.toString()));

        // Invalid build.properties file -> Modified class path could not be found
        Files.copy(new File(Objects.requireNonNull(classLoader.getResource( "UtilFiles/BuildPropertyFiles/modifiedClassCannotBeFound.properties")).getFile()).toPath(), buildPropertiesFile, StandardCopyOption.REPLACE_EXISTING);
        assertThrows(Exception.class, () -> ProjectPaths.getBuggyProgramPath(checkoutPath.toString()));

        deleteCheckout();
    }

    @Test
    @DisplayName("Get Fixed Program Path")
    public void testGetFixedProgramPath() throws Exception {
        // Valid Defects4J project exists
        assertEquals(ProjectPaths.getFixedProgramPath("Lang", 1).toString(), "/defects4j/framework/projects/Lang/patches/1.src.patch");

        // Invalid Defects4J Project does not exist
        assertThrows(IOException.class, () -> ProjectPaths.getFixedProgramPath("LangDoesNotExist", 1000));
    }

    @Test
    @DisplayName("Save bugs to file system")
    public void testSaveBugsToFileSystem() throws Exception {
        // Give value of Lang, 1, MutationOperator1, patches 0
        ProjectPaths.saveBugsToFileSystem("Lang", 1, "MutationOperator1", new ArrayList<>());
        File[] filesInOutput = new File("/output/Lang_1/MutationOperator1").listFiles();
        assertNotNull(filesInOutput);
        assertEquals(filesInOutput.length, 0);

        // Throw an exception if /output/Lang_1/MutationOperator1/ already exists
        assertThrows(FileAlreadyExistsException.class, () -> ProjectPaths.saveBugsToFileSystem("Lang", 1, "MutationOperator1", new ArrayList<>()));

        // Remove /output
        FileUtils.deleteDirectory(new File("/output"));

        // Give value of Lang, 1, MutationOperator1, patches 1
        ArrayList<CompilationUnit> patches = new ArrayList<>();
        patches.add(new CompilationUnit());
        ProjectPaths.saveBugsToFileSystem("Lang", 1, "MutationOperator1", patches);
        assertTrue(new File("/output/Lang_1/MutationOperator1/1").exists());

        // Remove /output
        FileUtils.deleteDirectory(new File("/output"));
    }
}