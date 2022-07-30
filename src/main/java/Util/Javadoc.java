package Util;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Javadoc class produces HTML documents for this project and outputs to the /javadoc directory.
 */
public final class Javadoc {

    /**
     * A command line call to maven javadoc is called and the apidocs directory containing the javadoc HTML files of the project are copied to the /javadoc directory.
     *
     * @exception Exception If the command line javadoc generation fails or the /apidocs directory cannot be copied
     */
    public static void main() throws Exception {
        // Run maven javadoc
        ShellProcessBuilder.runCommand(new String[]{"mvn", "javadoc:javadoc"}, new File("/APRFramework/"));
        
        // Assert that /APRFramework/target/site/apidocs exists
        Path javadocPath = Paths.get("/APRFramework/target/site/apidocs");
        if (!Files.exists(javadocPath)) { throw new Exception("Directory '" + javadocPath + "' does not exist"); }

        // Copy over javadocs from default directory to /javadoc
        FileUtils.copyDirectory(javadocPath.toFile(), new File("/javadoc"));
    }
}
