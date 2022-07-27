package Util;

import static org.junit.jupiter.api.Assertions.*;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestMutationHelpers {
    final String basePath = "/src/test/java/Util/MutationHelperFiles/";

    public CompilationUnit collectAndParseProgramFromFile(String path) {
        StaticJavaParser.getConfiguration().setAttributeComments(false);
        StaticJavaParser.getConfiguration().setSymbolResolver(new JavaSymbolSolver(new CombinedTypeSolver(new ReflectionTypeSolver())));

        return StaticJavaParser.parse(path);
    }

    @Test
    @DisplayName("Get method parameters")
    public void testMethodParams() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "MethodParams.java");
        List<MethodDeclaration> methodDeclarations = new ArrayList<>(program.findAll(MethodDeclaration.class));
        assertEquals(methodDeclarations.size(), 1);

        List<String> methodParams = MutationHelpers.getMethodParams(methodDeclarations.get(0).resolve());
        assertEquals(methodDeclarations.size(), 3);

        List<String> knownMethodParams = new ArrayList<>();
        knownMethodParams.add("String");
        knownMethodParams.add("String");
        knownMethodParams.add("String");

        assertEquals(knownMethodParams, methodParams);
    }

    @Test
    @DisplayName("Get constructor parameters")
    public void testConstructorParams() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "ConstructorParams.java");
        List<ConstructorDeclaration> constructorDeclarations = new ArrayList<>(program.findAll(ConstructorDeclaration.class));
        assertEquals(constructorDeclarations.size(), 1);

        List<String> constructorParams = MutationHelpers.getConstructorParams(constructorDeclarations.get(0).resolve());
        assertEquals(constructorParams.size(), 4);

        List<String> knownConstructorParams = new ArrayList<>();
        knownConstructorParams.add("int");
        knownConstructorParams.add("String");
        knownConstructorParams.add("Boolean");
        knownConstructorParams.add("Double");

        assertEquals(knownConstructorParams, constructorParams);
    }

    @Test
    @DisplayName("Compare line numbers")
    public void testCompareLineNumbers() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "CompareLineNumbers.java");
        List<VariableDeclarator> variableDeclarators = new ArrayList<>(program.findAll(VariableDeclarator.class));
        assertEquals(variableDeclarators.size(), 2);

        VariableDeclarator var1 = variableDeclarators.get(0);
        VariableDeclarator var2 = variableDeclarators.get(1);

        assertTrue(MutationHelpers.compareLineNumbers(var1.getBegin(), var2.getBegin()));
    }

    @Test
    @DisplayName("Get Required Types")
    public void testRequiredTypes() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "RequiredTypes.java");
        List<MethodDeclaration> methodDeclarations = new ArrayList<>(program.findAll(MethodDeclaration.class));
        assertEquals(methodDeclarations.size(), 2);

        MethodDeclaration method1 = methodDeclarations.get(0);
        List<String> params = new ArrayList<>();
        params.add("int");
        params.add("String");

        NodeList<Expression> requiredTypes = MutationHelpers.getRequiredTypes(method1, params);
        assertEquals(requiredTypes.size(), 2);

        NodeList<Expression> knownRequiredTypes = new NodeList<>();
        program.findAll(VariableDeclarator.class).forEach(i -> knownRequiredTypes.add(i.getNameAsExpression()));

        assertEquals(requiredTypes, knownRequiredTypes);
    }

    @Test
    @DisplayName("Method Implemented")
    public void testMethodImplemented() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "MethodImplemented.java");
        List<MethodDeclaration> methodDeclarations = new ArrayList<>(program.findAll(MethodDeclaration.class));
        assertEquals(methodDeclarations.size(), 5);

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
    public void testResolveCollection() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "ResolveCollection.java");
        List<ReturnStmt> returnStmts = new ArrayList<>(program.findAll(ReturnStmt.class));
        assertEquals(returnStmts.size(), 1);

        ReturnStmt node = returnStmts.get(0);
        assertEquals(MutationHelpers.resolveCollection(node, "String").size(), 2);
    }

    @Test
    @DisplayName("Resolve All Types in program")
    public void testResolveAllTypes() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "ResolveAllTypes.java");
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
    public void testGetChildrenOfExpressions() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "ChildrenOfExpression.java");
        List<ReturnStmt> returnStmts = new ArrayList<>(program.findAll(ReturnStmt.class));
        assertEquals(returnStmts.size(), 1);

        ReturnStmt node = returnStmts.get(0);
        List<Node> children = MutationHelpers.getChildrenOfExpression(node);

        assertEquals(children.size(), 2);
    }

    @Test
    @DisplayName("Get all statement expressions in program")
    public void testStatementExpressions() {
        CompilationUnit program = collectAndParseProgramFromFile(basePath + "StatementExpressions.java");
        List<Expression> statementExpressions = MutationHelpers.collectStatementExpressions(program);

        assertEquals(statementExpressions.size(), 2);
    }
}
