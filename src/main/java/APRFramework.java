import GP.GP.AbstractSyntaxTree;
import GP.GP.GPRunner;
import GP.MutationOperators.*;
import Util.Javadoc;
import Util.ParserRunner;
import Util.ValidDefectsPatches;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Paths;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        // Ensure clean setup
        File checkoutFolder = new File("/tmp/checkout/");
        File tempCheckoutFolder = new File("src/test/java/Util/Lang_1_test");

        if(checkoutFolder.exists()) { FileUtils.deleteDirectory(checkoutFolder); }
        if (tempCheckoutFolder.exists()) { FileUtils.deleteDirectory(tempCheckoutFolder); }

        // Call parserRunner
        ParserRunner.main("config.yml");

        // Call Javadoc
        if (ParserRunner.output.javadoc) { Javadoc.main(); }

        // Call PatchViewer
        if (ParserRunner.output.uploadToPatchViewer) { ValidDefectsPatches.main(); }

        // Call GPRunner
        if (ParserRunner.output.gp) { GPRunner.main(); }
    }
}