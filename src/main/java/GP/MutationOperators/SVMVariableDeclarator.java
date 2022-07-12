package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static GP.MutationOperators.SVM.getRequiredTypes;

public final class SVMVariableDeclarator {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        List<Node> nodes = new ArrayList<>(program.findAll(VariableDeclarator.class));

        if (nodes.size() == 0) { throw new Exception("No `Variable Declarator Expression` was found in the Compilation Unit"); }
        Node nodeFrom = nodes.get(GPHelpers.randomIndex(nodes.size()));
        Node nodeTo = getVariableDeclarator(nodeFrom, ((VariableDeclarator) nodeFrom).resolve().getType().describe());

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }
        return program.clone();
    }

    static Node getVariableDeclarator(Node nodeFrom, String fromType) {
        NodeList<Expression> arguments = getRequiredTypes(nodeFrom, new ArrayList<>(Collections.singleton(fromType)));

        if (arguments != null && arguments.size() > 0) {
            List<Node> nodes = new ArrayList<>(arguments);
            return ((VariableDeclarator) nodeFrom).clone().setInitializer((Expression) nodes.get(GPHelpers.randomIndex(nodes.size())));
        }

        return nodeFrom;
    }
}