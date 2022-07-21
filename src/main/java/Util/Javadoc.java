package Util;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Javadoc {

    public static void main() throws Exception {
        // Run maven javadoc
        ShellProcessBuilder.runCommand(new String[]{"mvn", "javadoc:javadoc"}, new File("/APRFramework/")).waitFor();
        
        // Assert that /APRFramework/target/site/apidocs exists
        Path javadocPath = Paths.get("/APRFramework/target/site/apidocs");
        if (!Files.exists(javadocPath)) { throw new Exception("Directory '" + javadocPath + "' does not exist"); }

        // Copy over javadocs from default directory to /javadoc
        FileUtils.copyDirectory(javadocPath.toFile(), new File("/javadoc"));
    }
}
