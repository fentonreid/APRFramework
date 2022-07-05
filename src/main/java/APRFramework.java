import GP.GP.AbstractSyntaxTree;
import GP.GP.GPRunner;
import GP.MutationOperators.*;
//import GP.MutationOperators.BER;
import Util.Javadoc;
import Util.ParserRunner;
import Util.ValidDefectsPatches;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.nio.file.Paths;

public class APRFramework {
    public static void main(String[] args) throws Exception {
        CompilationUnit mutationAST = AbstractSyntaxTree.generateAST(Paths.get("svm.java"));

        try {
            //BAR.mutate(mutationAST);
            //BER.mutate(mutationAST);
            //LRR.mutate(mutationAST);
            //WRM.mutate(mutationAST);
            SVM.mutate(mutationAST);
        } catch (Exception ex) {
            System.out.println("Exception was encountered handling gracefully " + ex);
        }

        // This keeps formatting the same, useful for diff comparisons of patch and fixed code...
        ////LexicalPreservingPrinter.setup(mutationAST);
        ////System.out.println(LexicalPreservingPrinter.print(mutationAST));

        // Ensure clean setup
        File outputFolder = new File("/output");
        if(outputFolder.exists()) { FileUtils.deleteDirectory(outputFolder); }

        /*// Call parserRunner
        ParserRunner.main("config.yml");

        // Call Javadoc
        System.out.println("JAVADOC??");
        if (ParserRunner.output.javadoc) { Javadoc.main(); }

        // Call PatchViewer
        System.out.println("PATCHES?");
        if (ParserRunner.output.patches) { ValidDefectsPatches.main(); }

        // Call GPRunner
        if (ParserRunner.output.gp) { GPRunner.main(); }*/

        // RUN IT
        // docker run -it -v $(pwd)/APRFramework:/APRFramework/  dev

        // Run maven
        // mvn compile exec:java -Dexec.mainClass="APRFramework"
    }
}