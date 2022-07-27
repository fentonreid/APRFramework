package GP.MutationOperators;

import GP.GP.UnmodifiedProgramException;
import Util.MutationHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ReturnStmt;
import java.util.ArrayList;
import java.util.List;

/**
 * The Generate Node Replacement (GNR) mutation is a fairly diverse mutation with many possible outcomes, entry points to the program which consist of variable declarations, field access expression, object creation expressions and method call expressions are collected.<br><br>
 * Their values are replaced with a node that is generated from the nodes available to the entry point in the local and limited global scope. Local and global scope collection occurs for nodes with the same type, in the case of method declarations this is the return type and for object creation expressions the class that is being created.<br><br>
 * The local scope is defined as a collection of allowed nodes that appear in a class and/or method before the definition of the entry point. For instance enum declarations, field variables, method declarations (if the method is implemented, cases such as abstract or non-static methods in interfaces are ignored), variable declarations, object creation expressions, field access expressions and method parameters of the method the entry point belong to are collected.<br><br>
 * The global scope is defined as allowed nodes outside the method and/or class of the entry point, for instance method declarations that are implemented are collected from the same and other classes in the Compilation Unit. For the latter, objects may need to be instantiated or if the method is static then the name of the Class.method() is used, field variables are also accessible in the same and other classes.<br><br>
 */
public final class GNR {

    /**
     * (1) Entry point collection takes place, meaning variable declarations, field access expressions, method call expressions and object creation expressions in the program are collected<br>
     * (2) If no allowed nodes in the program where found then the program is returned unmodified<br>
     * (3) Otherwise a different action is performed based on the node instance<br>
     *  (3.1) If the node is a variable declarator then the variable initialiser (e.g. i = 5, 5 is the initialiser), is switched with a node of the same type as determined by the variable type, in the local or global scope<br>
     *  (3.2) If the node is a method call expression then the whole expression is switched with a node of the same type as determined by the method calls return type, in the local or global scope<br>
     *  (3.3) If the node is a object creation expression then the whole expression is switched with a node of the same type as determined by the class the object belongs to, in the local or global scope<br>
     *  (3.4) If the node is a field access expression then the whole expression is switched with a node of the same type as determined by the type of the field, in the local or global scope<br>
     *  (3.5) If no nodes are available in the local or global scope<br>
     * (4) Once a switch has occurred then<br>
     *
     * @param program                           The AST representation of the program to mutate
     * @return                                  The mutated program is returned
     * @throws UnmodifiedProgramException       If the chosen mutation fails to mutate the program with a known reason
     */
    public static CompilationUnit mutate(CompilationUnit program) throws UnmodifiedProgramException {

        // Collect all listed nodes from the compilation unit
        List<Node> nodes = new ArrayList<>(collectExpressions(program));

        // Get random node from subset
        if (nodes.size() == 0) { throw new UnmodifiedProgramException("None of the required nodes could be found in the Compilation Unit"); }
        Node nodeFrom = nodes.get(MutationHelpers.randomIndex(nodes.size()));
        Node nodeTo = null;

        // Determine the node to change to
        List<Expression> expressions;
        switch (nodeFrom.getClass().getSimpleName()) {
            case "VariableDeclarator":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((VariableDeclarator) nodeFrom).resolve().getType().describe());
                if (expressions.size() > 0) { nodeTo = ((VariableDeclarator) nodeFrom).clone().setInitializer(expressions.get(MutationHelpers.randomIndex(expressions.size()))); }
                break;

            case "MethodCallExpr":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((MethodCallExpr) nodeFrom).resolve().getReturnType().describe());
                if (expressions.size() > 0) { nodeTo = expressions.get(MutationHelpers.randomIndex(expressions.size())); }
                break;

            case "ObjectCreationExpr":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((ObjectCreationExpr) nodeFrom).resolve().getClassName());
                if (expressions.size() > 0) { nodeTo = expressions.get(MutationHelpers.randomIndex(expressions.size())); }
                break;

            case "FieldAccessExpr":
                expressions = MutationHelpers.resolveCollection(nodeFrom, ((FieldAccessExpr) nodeFrom).resolve().getType().describe());
                if (expressions.size() > 0) { nodeTo = expressions.get(MutationHelpers.randomIndex(expressions.size())); }
                break;

            case "ReturnStmt":
                String type = ((ReturnStmt) nodeFrom).getExpression().isPresent()? ((ReturnStmt) nodeFrom).getExpression().get().calculateResolvedType().describe() : null;

                if (type == null) {
                    if (nodeFrom.findAncestor(MethodDeclaration.class).isPresent())
                        type = nodeFrom.findAncestor(MethodDeclaration.class).get().resolve().getReturnType().describe();
                    else
                        throw new UnmodifiedProgramException("Could not get type of return statement");
                }

                expressions = MutationHelpers.resolveCollection(nodeFrom, type);
                if (expressions.size() > 0) { nodeTo =  ((ReturnStmt) nodeFrom).setExpression(expressions.get(MutationHelpers.randomIndex(expressions.size()))); }
        }

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }

        return program.clone();
    }

    /**
     * Collect method call, field access, and object creation expressions, variable declarations and return statements from the current program.
     *
     * @param cu    The AST representation of the current program
     * @return      A List of collected nodes from the compilation unit that match the class
     */
    private static List<Node> collectExpressions(CompilationUnit cu) {
        List<Node> nodes = new ArrayList<>();
        nodes.addAll(cu.findAll(MethodCallExpr.class));
        nodes.addAll(cu.findAll(FieldAccessExpr.class));
        nodes.addAll(cu.findAll(ObjectCreationExpr.class));
        nodes.addAll(cu.findAll(VariableDeclarator.class));
        nodes.addAll(cu.findAll(ReturnStmt.class));

        return nodes;
    }
}