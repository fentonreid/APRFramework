package GP.GP;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The AbstractSyntaxTree class constructs an AST representation of a given program and sets the JavaParser symbol solver to read from the Defects4j bugs source directory.
 */
public final class AbstractSyntaxTree {

    public static CombinedTypeSolver combinedTypeSolver = null;
    public static List<Path> relevantPaths = null;

    /**
     * Construct from a given file a CompilationUnit that contains an AST representation of the Defects4j buggy program.
     *
     * @param buggyFile         The Defects4j program that we are constructing into an AST
     * @param basePath          The path to the checked out Defects4j bug
     * @param sourceFilePath    The source directory where all the Java files for a certain Defects4j bug are stored
     * @return                  An AST representation of the relevant buggy defects4j bug
     * @throws Exception        If the buggy file could not be found
     */
    public static CompilationUnit generateAST(Path buggyFile, String basePath, String sourceFilePath) throws Exception {
        try {
            combinedTypeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(sourceFilePath + "/"));

            // Find all jar files in the folder and add to Combined Type Solver
            ArrayList<String> jarPaths = (ArrayList<String>) Files.find(Paths.get(basePath), 1000, (p, a) -> p.toString().toLowerCase().endsWith(".jar")).map(path -> path.toString()).collect(Collectors.toList());

            if (jarPaths.size() > 0) { for (String path : jarPaths) { combinedTypeSolver.add(new JarTypeSolver(path)); } }
            
            // Remove comments and add symbol solving to the Static Java Parser
            StaticJavaParser.getConfiguration().setAttributeComments(false);
            StaticJavaParser.getConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

            return StaticJavaParser.parse(buggyFile);

        } catch (FileNotFoundException ex) { throw new Exception("Location of buggy file '" + buggyFile +"' could not be found"); }
    }
}