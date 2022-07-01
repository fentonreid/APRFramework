package GP.GP;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public final class AbstractSyntaxTree {
    public AbstractSyntaxTree() {}

    public static CompilationUnit generateAST(Path buggyFile) throws Exception {
        try {
            StaticJavaParser.getConfiguration().setAttributeComments(false);
            StaticJavaParser.getConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));
            StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.RAW);

            return StaticJavaParser.parse(buggyFile);
        } catch (FileNotFoundException ex) { throw new Exception("Location of buggy file '" + buggyFile +"' could not be found"); }
    }
}