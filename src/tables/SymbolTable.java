package tables;

import engine.ConstantAssignmentException;
import engine.ConstantNonAssignmentException;
import engine.UninitializedGlobalVariableException;
import engine.VariableAlreadyDeclaredException;
import java.util.*;

public class SymbolTable {
    private static final String NOT_DECLARED_IN_ANY_ACCESSIBLE_SCOPE = "Variable ' %s ' not declared " +
            "in any accessible scope";
    // Outer map: scope number -> (variable name -> (type, value, is constant))
    private final HashMap<Integer, HashMap<String, VariableInformation>> variablesMap
            = new HashMap<>();
    private int currentScope = 0;

    private static class VariableInformation {
        private final Stack<String> globalVariableValuesStack;
        private int lastUpdatedScope;
        String type;
        String value;
        boolean isConstant;

        private VariableInformation(String type, String value, boolean isConstant) {
            this.type = type;
            this.value = value;
            this.isConstant = isConstant;
            this.globalVariableValuesStack = new Stack<>();
            this.lastUpdatedScope = 0;
        }

    }

    public void enterScope() {
        currentScope++;
        variablesMap.put(currentScope, new HashMap<>());
    }

    public int getCurrentScope() {
        return currentScope;
    }



    public void exitScope() {
        HashMap<String, VariableInformation> globalVariables = variablesMap.get(1);
        if (globalVariables != null) {
            for (Map.Entry<String, VariableInformation> entry : globalVariables.entrySet()) {
                if (entry.getValue().lastUpdatedScope == getCurrentScope()) {
                    entry.getValue().globalVariableValuesStack.pop();
                    entry.getValue().lastUpdatedScope--;
                    entry.getValue().value = entry.getValue().globalVariableValuesStack.peek();
                }
            }
        }
        variablesMap.remove(currentScope);
        currentScope--;

    }

    public void declareVariable(String name, String type, String value, boolean isConstant,
                                boolean isParameter)
            throws VariableAlreadyDeclaredException, ConstantNonAssignmentException {
        HashMap<String, VariableInformation> currentScopeMap = variablesMap.get(currentScope);

        if (currentScopeMap.containsKey(name)) {
            throw new VariableAlreadyDeclaredException(name);
        }
        if (isConstant && value == null && !isParameter) {
            throw new ConstantNonAssignmentException(name);
        }

        currentScopeMap.put(name, new VariableInformation(type, value, isConstant));
        if (getCurrentScope() == 1) {
            variablesMap.get(1).get(name).globalVariableValuesStack.push(value);
            variablesMap.get(1).get(name).lastUpdatedScope++;
        }
    }

    public void assignValue(String name, String value) throws ConstantAssignmentException,
            UninitializedGlobalVariableException {
        int scope = findVariableScope(name); // might throw if not assigned
        if (scope == 1 && getCurrentScope() != 1 && getValue(name) == null) {
            throw new UninitializedGlobalVariableException(name);
        }
        if (variablesMap.get(scope).get(name).isConstant) {
            throw new ConstantAssignmentException(name);
        }
        VariableInformation Triple = variablesMap.get(scope).get(name);
        Triple.value = value;
        if (scope == 1) {
            variablesMap.get(scope).get(name).globalVariableValuesStack.push(value);
            variablesMap.get(scope).get(name).lastUpdatedScope++;
            System.out.println(variablesMap.get(scope).get(name).globalVariableValuesStack);


        }
    }

    public String getValue(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).value;
    }

    public String getType(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).type;
    }

    public int isVariableDeclared(String name) {
        for (int scope = currentScope; scope >= 1; scope--) {
            if (variablesMap.containsKey(scope) && variablesMap.get(scope).containsKey(name)) {
                return scope;
            }
        }
        return 0;
    }

    public boolean isConstant(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).isConstant;
    }

    // Helper function to find the scope where a variable is declared
    public int findVariableScope(String name) {
        for (int scope = currentScope; scope >= 1; scope--) {
            if (variablesMap.containsKey(scope) && variablesMap.get(scope).containsKey(name)) {
                return scope;
            }
        }
        throw new IllegalArgumentException(String.format(NOT_DECLARED_IN_ANY_ACCESSIBLE_SCOPE, name));
    }

}
