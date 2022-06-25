import Util.Javadoc;
import Util.ValidDefectsPatches;
import Util.ParserRunner;
import GP.GP.GPRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        // Ensure clean setup
        File outputFolder = new File("/output");
        if(outputFolder.exists()) { FileUtils.deleteDirectory(outputFolder); }

        // Call parserRunner
        ParserRunner.main("config.yml");

        // Call Javadoc
        System.out.println("JAVADOC??");
        if (ParserRunner.output.javadoc) { Javadoc.main(); }

        // Call PatchViewer
        System.out.println("PATCHES?");
        if (ParserRunner.output.patches) { ValidDefectsPatches.main(); }

        // Call GPRunner
        if (ParserRunner.output.gp) { GPRunner.main(); }

        // RUN IT
        // docker run -it -v $(pwd)/APRFramework:/APRFramework/  dev

        // Run maven
        // mvn compile exec:java -Dexec.mainClass="APRFramework"
    }
}