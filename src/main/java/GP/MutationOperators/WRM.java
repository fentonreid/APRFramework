package GP.MutationOperators;

import Util.GPHelpers;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import java.util.*;

public final class WRM {
    public static CompilationUnit mutate(CompilationUnit program) throws Exception {
        // Original methodDeclaration: List of overloaded methods with different parameters
        Map<Expression, List<ResolvedDeclaration>> overloadedExpressions = getOverloadedExpressions(program);

        // Ensure non-nullness then pick a random expression from the hashmap and a subsequent value from the list
        if (overloadedExpressions.size() == 0) { throw new NullPointerException("No valid overload found for any methods or classes with respective method or object creation calls"); }
        Expression randomExpression = (Expression) overloadedExpressions.keySet().toArray()[new Random().nextInt(overloadedExpressions.keySet().toArray().length)];

        ResolvedDeclaration randomOverload = overloadedExpressions.get(randomExpression).get(GPHelpers.randomIndex(overloadedExpressions.get(randomExpression).size()));
        NodeList<Expression> modifiedArguments = new NodeList<>();

        List<String> originalParams;
        List<String> overloadedParams;
        Expression originalNode;

        switch (randomExpression.getClass().getSimpleName()) {
            case "MethodCallExpr":
                // We are dealing with a ResolvedMethodDeclaration
                ResolvedMethodDeclaration rmd = (ResolvedMethodDeclaration) randomOverload;

                // Get original and overloaded parameter types
                originalParams = getMethodParams(randomExpression.asMethodCallExpr().resolve());
                overloadedParams = getMethodParams(rmd);

                // Clone the original method and remove all arguments
                originalNode = randomExpression.asMethodCallExpr().clone();

                for (String param : overloadedParams) {
                    // If the original method contains the overloaded methods type then add to arguments
                    if (originalParams.contains(param)) {
                        int foundIndex = originalParams.indexOf(param);
                            modifiedArguments.add(originalNode.asMethodCallExpr().getArgument(foundIndex));
                            originalParams.remove(foundIndex);
                            originalNode.asMethodCallExpr().getArgument(foundIndex).remove();

                    // Try to find a variable, method call or object creation expression with same type
                    } else {
                        List<Expression> resolvedNodes = getExpressionsInSpecificClassAndMethod(randomExpression, param);
                        if (resolvedNodes.size() == 0) { throw new NullPointerException("Could not resolved any variable, method call or object creation expression with the required type of: '" + param + "'"); }
                        modifiedArguments.add(resolvedNodes.get(GPHelpers.randomIndex(resolvedNodes.size())));
                    }
                }

                // Replace old mce with overloaded method
                randomExpression.asMethodCallExpr().setArguments(modifiedArguments);
                break;
            case "ObjectCreationExpr":
                ResolvedConstructorDeclaration rcd = (ResolvedConstructorDeclaration) randomOverload;

                // Get original and overloaded parameter types
                originalParams = getConstructorParams(randomExpression.asObjectCreationExpr().resolve());
                overloadedParams = getConstructorParams(rcd);

                // Clone the original method and remove all arguments
                originalNode = randomExpression.asObjectCreationExpr().clone();

                for (String param : overloadedParams) {
                    // If the original method contains the overloaded methods type then add to arguments
                    if (originalParams.contains(param)) {
                        int foundIndex = originalParams.indexOf(param);
                        modifiedArguments.add(originalNode.asObjectCreationExpr().getArgument(foundIndex));
                        originalParams.remove(foundIndex);
                        originalNode.asObjectCreationExpr().getArgument(foundIndex).remove();

                        // Try to find a variable, method call or object creation expression with same type
                    } else {
                        List<Expression> resolvedNodes = getExpressionsInSpecificClassAndMethod(randomExpression, param);
                        if (resolvedNodes.size() == 0) { throw new NullPointerException("Could not resolved any variable, method call or object creation expression with the required type of: '" + param + "'"); }
                        modifiedArguments.add(resolvedNodes.get(GPHelpers.randomIndex(resolvedNodes.size())));
                    }
                }

                // Replace old mce with overloaded method
                randomExpression.asObjectCreationExpr().setArguments(modifiedArguments);
                break;
            default:
                throw new TypeNotPresentException("No valid expression was found", null);
        }

        System.out.println(program);
        return program.clone();
    }

    public static Map<Expression, List<ResolvedDeclaration>> getOverloadedExpressions(CompilationUnit cu) {
        Map<Expression, List<ResolvedDeclaration>> overloadedExpressions = new HashMap<>();

        // We can have method call expressions and object creation e.g. person.getName() and new person();
        overloadedExpressions.putAll(getOverloadedMethods(cu));
        overloadedExpressions.putAll(getOverloadedConstructors(cu));

        return overloadedExpressions;
    }

    public static Map<Expression, List<ResolvedDeclaration>> getOverloadedMethods(CompilationUnit cu) {
        Map<Expression, List<ResolvedDeclaration>> methodDeclarations = new HashMap<>();

        // We can have method call expressions and object creation e.g. person.getName() and new person();
        List<MethodCallExpr> methodCallExprs = new ArrayList<>(cu.findAll(MethodCallExpr.class));

        // Resolve method calls
        for (MethodCallExpr expression : methodCallExprs) {
            // Resolve method declaration and check for overloaded methods
            ResolvedMethodDeclaration rmd = expression.resolve();
            List<ResolvedDeclaration> overloadedMethods = new ArrayList<>();

            // Get all method declarations that have the same: class name, method name and returnTypes that aren't the original method
            cu.findAll(MethodDeclaration.class).forEach(md -> {
                ResolvedMethodDeclaration overloadedRMD = md.resolve();
                if (!(overloadedRMD.getQualifiedSignature().equals(rmd.getQualifiedSignature())) && overloadedRMD.getQualifiedName().equals(rmd.getQualifiedName()) && overloadedRMD.getReturnType().describe().equals(rmd.getReturnType().describe())) {
                    overloadedMethods.add(overloadedRMD);
                }
            });

            if (overloadedMethods.size() > 0) { methodDeclarations.put(expression, overloadedMethods); }
        }

        return methodDeclarations;
    }

    public static Map<Expression, List<ResolvedDeclaration>> getOverloadedConstructors(CompilationUnit cu) {
        Map<Expression, List<ResolvedDeclaration>> validConstructors = new HashMap<>();

        List<ObjectCreationExpr> objectCreationExprs = new ArrayList<>(cu.findAll(ObjectCreationExpr.class));

        for (ObjectCreationExpr expression : objectCreationExprs) {
            // Resolve object creation constructor and check for overloaded constructors
            ResolvedConstructorDeclaration rcon = expression.resolve();
            List<ResolvedDeclaration> overloadedConstructors = new ArrayList<>();

            cu.getClassByName(rcon.getClassName()).ifPresent(i -> i.getConstructors().forEach(constructor -> {
                // If the constructor signatures are different then an overloaded constructor has been identified
                if (!(rcon.getQualifiedSignature().equals(constructor.resolve().getQualifiedSignature()))) {
                    overloadedConstructors.add(constructor.resolve());
                }
            }));

            if (overloadedConstructors.size() > 0) { validConstructors.put(expression, overloadedConstructors); }
        }

        return validConstructors;
    }

    public static List<Expression> getExpressionsInSpecificClassAndMethod(Expression expression, String resolvedType) {
        List<Expression> expressions = new ArrayList<>();

        // Get all field variables
        expression.findAncestor(ClassOrInterfaceDeclaration.class).get().findAll(FieldDeclaration.class).forEach(fd -> fd.getVariables().forEach(vd -> {
            if (vd.resolve().getType().describe().equals(resolvedType)) {
                expressions.add(vd.getNameAsExpression().clone());
            }
        }));

        // Get all method call expressions and check the return type-> person.getName()
        expression.findAncestor(MethodDeclaration.class).get().findAll(MethodCallExpr.class).forEach(mce -> {
            if (mce.resolve().getReturnType().describe().equals(resolvedType) && compareLineNumbers(mce.getBegin(), expression.getBegin())) {
                expressions.add(mce.clone());
            }
        });

        // Add all variable declarators, in Java a type must be declared, e.g. String name = "Fenton";
        expression.findAncestor(MethodDeclaration.class).get().findAll(VariableDeclarator.class).forEach(md -> {
            if (md.resolve().getType().describe().equals(resolvedType) && compareLineNumbers(md.getBegin(), expression.getBegin())) {
                expressions.add(md.getNameAsExpression().clone());
            }
        });

        // Add all object creation expr, e.g. new Person("Fenton");
        expression.findAncestor(MethodDeclaration.class).get().findAll(ObjectCreationExpr.class).forEach(oce -> {
            if (oce.resolve().getClassName().equals(resolvedType) && compareLineNumbers(oce.getBegin(), expression.getBegin())) {
                expressions.add(oce.clone());
            }
        });

        return expressions;
    }

    public static boolean compareLineNumbers(Optional<Position> position, Optional<Position> nodeDeclarationPosition) {
        if(!position.isPresent() || !nodeDeclarationPosition.isPresent()) { return false; }

        return position.get().line < nodeDeclarationPosition.get().line;
    }

    public static List<String> getMethodParams(ResolvedMethodDeclaration method) {
        List <String> params = new ArrayList<>();
        for (int i = 0; i < method.getNumberOfParams(); i++) {
            params.add(method.getParam(i).getType().describe());
        }

        return params;
    }

    public static List<String> getConstructorParams(ResolvedConstructorDeclaration constructor) {
        List <String> params = new ArrayList<>();
        for (int i = 0; i < constructor.getNumberOfParams(); i++) {
            params.add(constructor.getParam(i).getType().describe());
        }

        return params;
    }
}