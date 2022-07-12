package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import java.util.ArrayList;
import java.util.List;
import static GP.MutationOperators.SVM.getRequiredTypes;
import static GP.MutationOperators.SVM.getConstructorParams;

public final class SVMObjectCreation {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        List<Node> nodes = new ArrayList<>(program.findAll(ObjectCreationExpr.class));

        if (nodes.size() == 0) { throw new Exception("No `Object Creation Expression` was found in the Compilation Unit"); }
        Node nodeFrom = nodes.get(GPHelpers.randomIndex(nodes.size()));
        Node nodeTo = getObjectCreationExpr(nodeFrom, program);

        if(nodeTo != null) { nodeFrom.replace(nodeTo); }
        return program.clone();
    }

    static Node getObjectCreationExpr(Node nodeFrom, CompilationUnit cu) {
        List<Node> nodes = new ArrayList<>();

        // Resolve the object and get all constructors
        ResolvedConstructorDeclaration rcon = ((ObjectCreationExpr) nodeFrom).resolve();

        cu.getClassByName(rcon.getClassName()).ifPresent(i -> i.getConstructors().forEach(constructor -> {
            // Fill the parameters for each constructor
            ObjectCreationExpr currentOCE = ((ObjectCreationExpr) nodeFrom).clone();
            currentOCE.getArguments().removeAll(currentOCE.getArguments());
            NodeList<Expression> arguments = getRequiredTypes(nodeFrom, getConstructorParams(constructor.resolve()));

            if (arguments != null) {
                currentOCE.setArguments(arguments);
                nodes.add(currentOCE);
            }
        }));

        return nodes.get(GPHelpers.randomIndex(nodes.size()));
    }
}