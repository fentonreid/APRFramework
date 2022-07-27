import GP.GP.AbstractSyntaxTree;
import GP.GP.GPRunner;
import GP.MutationOperators.*;
import Util.Javadoc;
import Util.ParserRunner;
import Util.ValidDefectsPatches;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Paths;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        //CompilationUnit mutationAST = AbstractSyntaxTree.generateAST(Paths.get("mutationTest.java"), "");

        //BEM.mutate(mutationAST);
        //BAR.mutate(mutationAST);
        //BER.mutate(mutationAST);
        //BERExpansion.mutate(mutationAST);
        //BERReduction.mutate(mutationAST);
        //LRRelocation.mutate(mutationAST);
        //LRRemoval.mutate(mutationAST);
        //LRRelocation.mutate(mutationAST);
        //LRR.mutate(mutationAST);
        //GNR.mutate(mutationAST);

        // Ensure clean setup
        //File checkoutFolder = new File("/tmp/checkout/");
        //File outputFolder = new File("/output/");
        File tempCheckoutFolder = new File("src/test/java/Util/Lang_1_test");

        //if(checkoutFolder.exists()) { FileUtils.deleteDirectory(checkoutFolder); }
        //if(outputFolder.exists()) { FileUtils.deleteDirectory(outputFolder); }
        if (tempCheckoutFolder.exists()) { FileUtils.deleteDirectory(tempCheckoutFolder); }

        // Call parserRunner
        //ParserRunner.main("config.yml");

        // Call Javadoc
        //if (ParserRunner.output.javadoc) { Javadoc.main(); }

        // Call PatchViewer
        //if (ParserRunner.output.uploadToPatchViewer) { ValidDefectsPatches.main(); }

        // Call GPRunner
        //if (ParserRunner.output.gp) { GPRunner.main(); }

        // Call Generate Patches
        //if (ParserRunner.output.uploadToGeneratePatches) { }//ValidDefectsPatches.main(); }

        // Docker run command to copy over the users config.yml if present and map output and javadoc directories
        // docker run -v ${pwd}/config.yml:/APRFramework/config.yml -v ${pwd}/output/:/output/ -v ${pwd}/javadoc/:/javadoc/ dev  -> command to users

        //  docker run -v ${pwd}/:/APRFramework dev          -> debugging
    }
}