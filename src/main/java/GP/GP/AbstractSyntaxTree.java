package GP.GP;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public final class AbstractSyntaxTree {
    public AbstractSyntaxTree() {}

    public static CompilationUnit generateAST(Path buggyFile) throws Exception {
        try {
            StaticJavaParser.getConfiguration().setAttributeComments(false);
            return StaticJavaParser.parse(buggyFile);
        } catch (FileNotFoundException ex) { throw new Exception("Location of buggy file '" + buggyFile +"' could not be found"); }
    }
}