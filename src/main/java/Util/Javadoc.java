package Util;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class Javadoc {
    public Javadoc() {}

    public static void main() throws Exception {
        System.out.println("JAVADOC ::::::");

        // Run maven javadoc
        Process javadocProcess = ShellProcessBuilder.runCommand(new String[]{"mvn", "javadoc:javadoc"}, new File("/APRFramework"));

        // Assert that /APRFramework/target/site/apidocs exists
        Path javadocPath = Paths.get("/APRFramework/target/site/apidocs");
        if (!Files.exists(javadocPath)) { throw new Exception("Directory '" + javadocPath + "' does not exist"); }

        // Copy over javadocs from default directory to /javadoc
        FileUtils.copyDirectory(javadocPath.toFile(), new File("/javadoc"));
    }
}
