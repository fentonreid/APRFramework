import GP.GP.AbstractSyntaxTree;
import GP.MutationOperators.*;
//import GP.MutationOperators.BER;
import com.github.javaparser.ast.CompilationUnit;

import java.nio.file.Paths;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        CompilationUnit mutationAST = AbstractSyntaxTree.generateAST(Paths.get("MutationTests/svm.java"));

        //BEM.mutate(mutationAST);
        //BAR.mutate(mutationAST);
        //BER.mutate(mutationAST);
        //BERExpansion.mutate(mutationAST);
        //BERRemoval.mutate(mutationAST);
        //LRRelocation.mutate(mutationAST);
        //LRRemoval.mutate(mutationAST);
        //LRRelocation.mutate(mutationAST);
        //LRR.mutate(mutationAST);
        SVM.mutate(mutationAST);

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