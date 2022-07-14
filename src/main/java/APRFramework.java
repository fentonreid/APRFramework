import GP.GP.AbstractSyntaxTree;
import GP.GP.GPRunner;
import GP.MutationOperators.*;
//import GP.MutationOperators.BER;
import Util.Javadoc;
import Util.ParserRunner;
import Util.ShellProcessBuilder;
import Util.ValidDefectsPatches;
import YAMLParser.Defects4J;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        CompilationUnit mutationAST = AbstractSyntaxTree.generateAST(Paths.get("MutationTests/berAddition.java"));
        
        //BAR.mutate(mutationAST);
        //BER.mutate(mutationAST);
        BERAddition.mutate(mutationAST);
        //BERRemoval.mutate(mutationAST);
        //LRelocation.mutate(mutationAST);
        //LRemoval.mutate(mutationAST);
        //LRRelocation.mutate(mutationAST);
        //System.out.println(LRR.mutate(mutationAST));
        //WRM.mutate(mutationAST);
        //SVM.mutate(mutationAST);

        // This keeps formatting the same, useful for diff comparisons of patch and fixed code...
        ////LexicalPreservingPrinter.setup(mutationAST);
        ////System.out.println(LexicalPreservingPrinter.print(mutationAST));
        
        /*// Call parserRunner
        ParserRunner.main("config.yml");

        // Call Javadoc
        if (ParserRunner.output.javadoc) { Javadoc.main(); }

        // Call PatchViewer
        if (ParserRunner.output.patches) { ValidDefectsPatches.main(); }

        // Call GPRunner
        if (ParserRunner.output.gp) { GPRunner.main(); }*/

        // Docker run command to copy over the users config.yml if present and map output and javadoc directories
        // docker run -v ${pwd}/config.yml:/APRFramework/config.yml -v ${pwd}/output/:/output/ -v ${pwd}/javadoc/:/javadoc/ dev  -> command to users

        //  docker run -v ${pwd}/:/APRFramework dev          -> debugging
    }
}