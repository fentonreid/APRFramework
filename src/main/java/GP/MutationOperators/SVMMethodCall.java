package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import java.util.ArrayList;
import java.util.List;
import static GP.MutationOperators.SVM.getRequiredTypes;
import static GP.MutationOperators.SVM.getMethodParams;

public final class SVMMethodCall {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        List<Node> nodes = new ArrayList<>(program.findAll(MethodCallExpr.class));

        if (nodes.size() == 0) { throw new Exception("No `Method Call Expression` was found in the Compilation Unit"); }
        Node nodeFrom = nodes.get(GPHelpers.randomIndex(nodes.size()));
        Node nodeTo = getMethodCallExpr(nodeFrom, ((MethodCallExpr) nodeFrom).resolve().getReturnType().describe(), program);

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }
        return program.clone();
    }

    static Node getMethodCallExpr(Node nodeFrom, String fromType, CompilationUnit cu) {
        // Switch method calls with ones in local with same return type
        List<Node> nodes = new ArrayList<>();

        // Get all methods in the CompilationUnit with same return type and resolve parameters
        cu.findAll(MethodDeclaration.class).forEach(md -> {
            // If the return type of the method call matches then
            if (md.resolve().getReturnType().describe().equals(fromType)) {
                MethodCallExpr currentMCE = new MethodCallExpr().setName(md.resolve().getName());
                NodeList<Expression> arguments = getRequiredTypes(nodeFrom, getMethodParams(md.resolve()));

                if (arguments != null) {
                    currentMCE.setArguments(arguments);
                    nodes.add(currentMCE);
                }
            }
        });

        return nodes.get(GPHelpers.randomIndex(nodes.size()));
    }
}