package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import java.util.*;

public final class WRM {
    public static CompilationUnit mutate(CompilationUnit program) {
        // Original methodDeclaration: List of overloaded methods with different parameters
        Map<MethodCallExpr, List<ResolvedMethodDeclaration>> methodDeclarations = GetOverloadedMethods(program);

        // Pick random MethodCallExpr from Map
        MethodCallExpr mce = (MethodCallExpr) methodDeclarations.keySet().toArray()[new Random().nextInt(methodDeclarations.keySet().toArray().length)];
        ResolvedMethodDeclaration rmce = mce.resolve();

        // Pick at random an overloaded method to convert
        List<ResolvedMethodDeclaration> resolvedMethodDeclarations = methodDeclarations.get(mce);
        ResolvedMethodDeclaration rmd = resolvedMethodDeclarations.get(GPHelpers.randomIndex(resolvedMethodDeclarations.size()));

        // Get ResolvedMethodCallExpr types
        List < String > rmceParams = new ArrayList<>();
        for (int i = 0; i < rmce.getNumberOfParams(); i++) {
            rmceParams.add(rmce.getParam(i).getType().describe());
        }

        // Get overloaded type method
        List<String> rmdParams = new ArrayList<>();
        for (int i = 0; i < rmd.getNumberOfParams(); i++) {
            rmdParams.add(rmd.getParam(i).getType().describe());
        }

        // Clone the original mce and remove all arguments
        MethodCallExpr modifiedMCE = mce.clone();
        MethodCallExpr oldMCE = mce.clone();
        NodeList<Expression> modifiedArguments = new NodeList<>();
        modifiedMCE.setArguments(modifiedArguments);

        outer:
        for (String param : rmdParams) {
            // If param type is same as in the original then add it and remove from original mce
            for (int i = 0; i < oldMCE.getArguments().size(); i++) {
                // If parameter type was found in mce then add a new parameter to modifiedMCE and remove from the original mce
                if (rmceParams.get(i).equals(param)) {
                    modifiedArguments.add(oldMCE.getArgument(i));
                    oldMCE.getArguments().remove(oldMCE.getArgument(i));
                    continue outer;
                }
            }
            // Try to find a variable, method call or object creation expression with same type
            List<Expression> resolvedNodes = resolveType(program, param);

            if (resolvedNodes.size() == 0) { throw new NullPointerException("Could not resolved any variable, method call or object creation expression with the required type of: '" + param + "'"); }
            modifiedArguments.add(resolvedNodes.get(GPHelpers.randomIndex(resolvedNodes.size())));
        }

        System.out.println("MODIFIED ARGUMENTS: " + modifiedArguments);

        // Replace old mce with overloaded method
        modifiedMCE.setArguments(modifiedArguments);
        mce.replace(modifiedMCE);

        System.out.println(program);
        return program.clone();
    }

    public static List<Expression> resolveType(CompilationUnit cu, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();

        // Get all method call expressions and check the return type-> person.getName()
        cu.findAll(MethodCallExpr.class).forEach(mce -> {
            System.out.println("METHODS: " + mce);
            // If return type of method is of resolveType
            if(mce.resolve().getReturnType().describe().equals(resolvedType)) {
                expressions.add(mce.clone());
            }
        });

        // Add all variable declarators, in Java a type must be declared, e.g. String name = "Fenton";
        cu.findAll(VariableDeclarator.class).forEach(vd -> {
            if(vd.resolve().getType().describe().equals(resolvedType)) {
                expressions.add(vd.getNameAsExpression().clone());
            }
        });

        // Add all object creation expr, e.g. new Person("Fenton");
        cu.findAll(ObjectCreationExpr.class).forEach(oce -> {
            if(oce.resolve().getClassName().equals(resolvedType)) {
                expressions.add(oce.clone());
            }
        });

        // NOTE::
        // For expressions:
        //     int person = new Person("Fenton");
        // Both person and new Person("Fenton") are added, this is intentional to allow for object creation to be passed into a variable without assignment

        System.out.println(expressions);
        return expressions;
    }

    public static Map<MethodCallExpr, List<ResolvedMethodDeclaration>> GetOverloadedMethods(CompilationUnit cu) {
        Map<MethodCallExpr, List<ResolvedMethodDeclaration>> methodDeclarations = new HashMap<>();

        // Add to a list all methodCalls
        List<MethodCallExpr> methodCalls = new ArrayList<>(cu.findAll(MethodCallExpr.class));

        // We go through each method call and look for a different one with same class name, method name and return type but different parameters
        for (MethodCallExpr mce : methodCalls) {
            ResolvedMethodDeclaration rmd = mce.resolve();
            List<ResolvedMethodDeclaration> resolvedOverloadedMethods = new ArrayList<>();

            String className = rmd.getClassName();
            String methodName = rmd.getName();
            String returnType = rmd.getReturnType().describe();
            List<String> parameterTypes = new ArrayList<>();
            for(int i=0; i<rmd.getNumberOfParams(); i++) {
                parameterTypes.add(rmd.getParam(i).describeType());
            }

            // Get the method declaration that has the same className, methodName and returnTypes and numberOfParams is different
            cu.findAll(MethodDeclaration.class).forEach(md -> {
                ResolvedMethodDeclaration resolvedMethodDeclaration = md.resolve();
                if (resolvedMethodDeclaration.getClassName().equals(className) && resolvedMethodDeclaration.getName().equals(methodName) && resolvedMethodDeclaration.getReturnType().describe().equals(returnType) && resolvedMethodDeclaration.getNumberOfParams() != parameterTypes.size()) {
                    resolvedOverloadedMethods.add(md.resolve());
                }
            });

            if (resolvedOverloadedMethods.size() > 0) { methodDeclarations.put(mce, resolvedOverloadedMethods); }
        }

        if (methodDeclarations.size() == 0) { throw new NullPointerException("No valid method overload was found for the methods called in this program"); }

        return methodDeclarations;
    }
}