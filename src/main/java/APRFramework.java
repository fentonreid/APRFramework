package main.java;

import main.java.Util.ParserRunner;
import main.java.Util.ProjectPaths;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        // Call parserRunner
        ParserRunner parser = new ParserRunner();
        parser.main();

        // Get project paths
        ProjectPaths projectPaths = new ProjectPaths(parser.defects4j.selectedTestCases, parser.gp.iterationsPerBug, parser.gp.mutationOperators);
    }
}