package main.java;

import Util.CSVOutput;
import main.java.Util.ParserRunner;
import main.java.GP.GP.GPRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        // Ensure clean setup
        File outputFolder = new File("/output");
        if(outputFolder.exists()) { FileUtils.deleteDirectory(outputFolder); }

        // Call parserRunner
        ParserRunner.main("config.yml");

        // Call GPRunner
        GPRunner.main();
        
        // Run maven
        // mvn compile exec:java -Dexec.mainClass="main.java.APRFramework"
    }
}