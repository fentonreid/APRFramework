package Util;

import static org.junit.jupiter.api.Assertions.*;

import GP.Util;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TestMutationHelpers {
    final String basePath = "UtilFiles/MutationHelperFiles/";
    ClassLoader classLoader = Util.class.getClassLoader();

    public CompilationUnit collectAndParseProgramFromFile(File file) throws IOException {
        StaticJavaParser.getConfiguration().setAttributeComments(false);
        StaticJavaParser.getConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));

        return StaticJavaParser.parse(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Get method parameters")
    public void testMethodParams() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "MethodParams.java")).getFile()));
        List<MethodDeclaration> methodDeclarations = new ArrayList<>(program.findAll(MethodDeclaration.class));
        assertEquals(methodDeclarations.size(), 1);

        List<String> methodParams = MutationHelpers.getMethodParams(methodDeclarations.get(0).resolve());
        assertEquals(methodParams.size(), 3);

        List<String> knownMethodParams = new ArrayList<>();
        knownMethodParams.add("java.lang.String");
        knownMethodParams.add("java.lang.String");
        knownMethodParams.add("java.lang.String");

        assertEquals(knownMethodParams, methodParams);
    }

    @Test
    @DisplayName("Get constructor parameters")
    public void testConstructorParams() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "ConstructorParams.java")).getFile()));

        List<ConstructorDeclaration> constructorDeclarations = new ArrayList<>(program.findAll(ConstructorDeclaration.class));
        assertEquals(constructorDeclarations.size(), 1);

        List<String> constructorParams = MutationHelpers.getConstructorParams(constructorDeclarations.get(0).resolve());
        assertEquals(constructorParams.size(), 4);

        List<String> knownConstructorParams = new ArrayList<>();
        knownConstructorParams.add("int");
        knownConstructorParams.add("java.lang.String");
        knownConstructorParams.add("java.lang.Boolean");
        knownConstructorParams.add("java.lang.Double");

        assertEquals(knownConstructorParams, constructorParams);
    }


    @Test
    @DisplayName("Compare line numbers")
    public void testCompareLineNumbers() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "CompareLineNumbers.java")).getFile()));

        List<VariableDeclarator> variableDeclarators = new ArrayList<>(program.findAll(VariableDeclarator.class));
        assertEquals(variableDeclarators.size(), 2);

        VariableDeclarator var1 = variableDeclarators.get(0);
        VariableDeclarator var2 = variableDeclarators.get(1);

        assertTrue(MutationHelpers.compareLineNumbers(var1.getBegin(), var2.getBegin()));
    }

    @Test
    @DisplayName("Get Required Types")
    public void testRequiredTypes() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "RequiredTypes.java")).getFile()));

        List<MethodCallExpr> methodCallExpressions = new ArrayList<>(program.findAll(MethodCallExpr.class));
        assertEquals(methodCallExpressions.size(), 1);

        MethodCallExpr mce = methodCallExpressions.get(0);
        List<String> params = new ArrayList<>();
        params.add("int");
        params.add("java.lang.String");

        NodeList<Expression> requiredTypes = MutationHelpers.getRequiredTypes(mce, params);

        assertEquals(requiredTypes.size(), 2);

        NodeList<Expression> knownRequiredTypes = new NodeList<>();
        program.findAll(VariableDeclarator.class).forEach(i -> knownRequiredTypes.add(i.getNameAsExpression()));

        assertEquals(requiredTypes, knownRequiredTypes);
    }

    @Test
    @DisplayName("Method Implemented")
    public void testMethodImplemented() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "MethodImplemented.java")).getFile()));

        List<MethodDeclaration> methodDeclarations = new ArrayList<>(program.findAll(MethodDeclaration.class));
        assertEquals(methodDeclarations.size(), 6);

        // Interface :: Static and Non-Static
        assertTrue(MutationHelpers.methodImplemented(methodDeclarations.get(0)));
        assertFalse(MutationHelpers.methodImplemented(methodDeclarations.get(1)));

        // Abstract - Static and Abstract
        assertTrue(MutationHelpers.methodImplemented(methodDeclarations.get(2)));
        assertFalse(MutationHelpers.methodImplemented(methodDeclarations.get(3)));

        // Normal - Static and Non-Static
        assertTrue(MutationHelpers.methodImplemented(methodDeclarations.get(4)));
        assertTrue(MutationHelpers.methodImplemented(methodDeclarations.get(5)));
    }

    @Test
    @DisplayName("Resolve Collection in program")
    public void testResolveCollection() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "ResolveCollection.java")).getFile()));

        List<ReturnStmt> returnStmts = new ArrayList<>(program.findAll(ReturnStmt.class));
        assertEquals(returnStmts.size(), 1);

        ReturnStmt node = returnStmts.get(0);
        assertEquals(MutationHelpers.resolveCollection(node, "int").size(), 2);
    }


    @Test
    @DisplayName("Resolve All Types in program")
    public void testResolveAllTypes() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "ResolveAllTypes.java")).getFile()));

        List<ReturnStmt> returnStmts = new ArrayList<>(program.findAll(ReturnStmt.class));
        assertEquals(returnStmts.size(), 1);

        ReturnStmt node = returnStmts.get(0);
        MutationHelpers.resolveAllTypes(node);

        HashMap<String, List<Expression>> resolveTypes = new HashMap<>(MutationHelpers.resolveAllTypes(node));

        assertTrue(resolveTypes.containsKey("EmotionEnum"));
        assertTrue(resolveTypes.containsKey("int"));
    }


    @Test
    @DisplayName("Get all children of binary expressions")
    public void testGetChildrenOfExpressions() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "ChildrenOfExpression.java")).getFile()));

        List<ReturnStmt> returnStmts = new ArrayList<>(program.findAll(ReturnStmt.class));
        assertEquals(returnStmts.size(), 1);

        ReturnStmt node = returnStmts.get(0);
        assertTrue(node.getExpression().isPresent());

        List<Node> children = MutationHelpers.getChildrenOfExpression(node.getExpression().get());
        assertEquals(children.size(), 2);
    }

    @Test
    @DisplayName("Get all statement expressions in program")
    public void testStatementExpressions() throws IOException {
        CompilationUnit program = collectAndParseProgramFromFile(new File(Objects.requireNonNull(classLoader.getResource( basePath + "StatementExpressions.java")).getFile()));
        
        List<Expression> statementExpressions = MutationHelpers.collectStatementExpressions(program);
        assertEquals(statementExpressions.size(), 2);
    }
}
