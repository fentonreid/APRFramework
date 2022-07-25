package GP.GP;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * The AbstractSyntaxTree class constructs an AST representation of a given program and sets the JavaParser symbol solver to read from the Defects4j bugs source directory.
 */
public final class AbstractSyntaxTree {

    /**
     * Construct from a given file a CompilationUnit that contains an AST representation of the Defects4j buggy program.
     *
     * @param buggyFile         The Defects4j program that we are constructing into an AST
     * @param sourceDirectory   The source directory where all the Java files for a certain Defects4j bug are stored
     * @return                  An AST representation of the relevant buggy defects4j bug
     * @throws Exception        If the buggy file could not be found
     */
    public static CompilationUnit generateAST(Path buggyFile, String sourceDirectory) throws Exception {
        try {
            StaticJavaParser.getConfiguration().setAttributeComments(false);
            StaticJavaParser.getConfiguration().setSymbolResolver(new JavaSymbolSolver(new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(sourceDirectory))));

            return StaticJavaParser.parse(buggyFile);

        } catch (FileNotFoundException ex) { throw new Exception("Location of buggy file '" + buggyFile +"' could not be found"); }
    }
}