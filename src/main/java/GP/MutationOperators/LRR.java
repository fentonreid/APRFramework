package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import java.util.ArrayList;
import java.util.List;

public final class LRR {
    public static CompilationUnit mutate(CompilationUnit program) {
        // Remove a statement or expression from the program
        List<Node> nodeList = nodeCollector(program);
        Node removeNode = nodeList.get(GPHelpers.randomIndex(nodeList.size()));

        switch(removeNode.getClass().getSimpleName()) {
            case "TryStmt":
                TryStmt tryStmt = (TryStmt) removeNode;

                // Collect the try statement, catch statements and finally statements
                List<Node> tryNodes = new ArrayList<>();
                tryNodes.add(tryStmt.getTryBlock());
                tryNodes.addAll(tryStmt.getCatchClauses());
                tryStmt.getFinallyBlock().ifPresent(tryNodes::add);

                Node removeTryNode = tryNodes.get(GPHelpers.randomIndex(tryNodes.size()));
                removeTryNode.removeForced();
            default:
                removeNode.removeForced();
        }

        System.out.println(program);

        return program.clone();
    }

    public static List<Node> nodeCollector(CompilationUnit cu) {
        List<Node> nodeList = new ArrayList<>();
        List<Class<?>> allowedNodeTypes = new ArrayList<>();
        allowedNodeTypes.add(AssignExpr.class);
        allowedNodeTypes.add(BreakStmt.class);
        allowedNodeTypes.add(TryStmt.class);
        allowedNodeTypes.add(ContinueStmt.class);
        allowedNodeTypes.add(DoStmt.class);
        allowedNodeTypes.add(FieldDeclaration.class);
        allowedNodeTypes.add(ForStmt.class);
        allowedNodeTypes.add(ForEachStmt.class);
        allowedNodeTypes.add(IfStmt.class);
        allowedNodeTypes.add(LambdaExpr.class);
        allowedNodeTypes.add(MarkerAnnotationExpr.class);
        allowedNodeTypes.add(ExpressionStmt.class);
        allowedNodeTypes.add(ReturnStmt.class);
        allowedNodeTypes.add(SwitchEntry.class);
        allowedNodeTypes.add(ThrowStmt.class);
        allowedNodeTypes.add(WhileStmt.class);

        // Probably want a list of allowed nodes to remove,
        // AssignExpr                   :) (Simple enough e.g. x = x + 5;)
        // BreakStmt                    :) (Simple enough)
        // ConditionalExpr              :) (Covered by assignment)
        // ContinueStmt                 :) (Simple enough)
        // DoStmt                       :) (Currently just removes, do and while, think this is acceptable behaviour)
        // FieldDeclaration             :) (Will remove all fields, not just one e.g.  public boolean test = true, test2 = true; (this is ok for this mutation))
        // ForeachStmt                  :) (Will remove the whole expression)
        // ForStmt                      :O (Will remove the whole expression)
        // IfStmt                       :) (Will remove children below it)
        // LambdaExpr                   :) (Will remove the body e.g. numbers.forEach( (n) -> { System.out.println(n); } ); -> numbers.forEach())
        // MarkerAnnotationExpr         :) (Will remove any annotation)
        // ExpressionExpr               :) (Will remove -> System.out.println("testing");)
        // NameExpr                     :) (Is child of expression stmt -> int x = a + 3;)
        // NullLiteralExpr              :) (Covered by expression stmt)
        // ObjectCreationExpr           :) (Covered by expression stmt)
        // ReturnStmt                   :) (Will remove -> return; && return x > 5;, etc...)
        // SuperExpr                    :) (Covered by expression stmt -> super.eat();)
        // SwitchEntryStmts             :) (Will remove an entire case with body)
        // ThisExpr                     :) (Covered by expression stmt)
        // ThrowStmt                    :) (Remove statement)
        // TryStmt                      :) (Split into, try, catch, finally, currently if try all is deleted)
        // UnaryExpr (i++)              :) (Covered by expression stmt)
        // VariableDeclarator           :) (Covered by expression stmt)
        // WhileStmt                    :) (Will remove block and all contained code)

        // Add all nodes of the AST excluding certain node types we want to skip
        cu.walk(Node.TreeTraversal.PREORDER, node -> {
            if (allowedNodeTypes.contains(node.getClass())) {
                System.out.println("YAY" + node.getClass());
                nodeList.add(node);
            }
        });

        return nodeList;
    }
}