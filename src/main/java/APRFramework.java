package main.java;

import main.java.Util.ParserRunner;
import main.java.GP.GP.GPRunner;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        // Call parserRunner
        ParserRunner.main("config.yml");

        // Call GPRunner
        GPRunner.main();
        
        // Run maven
        // mvn compile exec:java -Dexec.mainClass="main.java.APRFramework"
    }
}