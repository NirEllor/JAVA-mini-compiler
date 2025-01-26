package tables;

import engine.ConstantAssignmentException;
import engine.ConstantNonAssignmentException;
import engine.UninitializedGlobalVariableException;
import engine.VariableAlreadyDeclaredException;
import java.util.*;

/**
 * A SymbolTable is used to manage variables across multiple scopes during execution.
 * It supports variable declaration, assignment, and retrieval, while enforcing rules
 * for constants, global variables, and scope-specific variable access.
 */
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


    /**
     * Enters a new scope by incrementing the current scope counter and initializing
     * a new variable map for the scope.
     */
    public void enterScope() {
        currentScope++;
        variablesMap.put(currentScope, new HashMap<>());
    }
    /**
     * Gets the current scope in the code, minimum 1 (global)
     */
    public int getCurrentScope() {
        return currentScope;
    }

    /**
     * Exits the current scope, removing all variables declared in this scope and
     * restoring global variables to their previous values if updated within this scope.
     */
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

    /**
     * Declares a new variable in the current scope.
     *
     * @param name        the name of the variable
     * @param type        the type of the variable
     * @param value       the initial value of the variable
     * @param isConstant  whether the variable is constant
     * @param isParameter whether the variable is a function parameter
     * @throws VariableAlreadyDeclaredException if the variable is already declared in the current scope
     * @throws ConstantNonAssignmentException   if a constant variable is declared without an initial value
     */
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

    /**
     * Assigns a new value to a variable. Updates the global variable stack if the variable is global.
     *
     * @param name  the name of the variable
     * @param value the new value to assign
     * @throws ConstantAssignmentException          if the variable is constant
     * @throws UninitializedGlobalVariableException if the variable is global and uninitialized
     */
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

    /**
     * Retrieves the current value of a variable.
     *
     * @param name the name of the variable
     * @return the current value of the variable
     */
    public String getValue(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).value;
    }

    /**
     * Retrieves the type of variable.
     *
     * @param name the name of the variable
     * @return the type of the variable
     */
    public String getType(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).type;
    }

    /**
     * Checks if a variable is declared in any accessible scope.
     *
     * @param name the name of the variable
     * @return the scope number where the variable is declared, or 0 if not declared
     */
    public int isVariableDeclared(String name) {
        for (int scope = currentScope; scope >= 1; scope--) {
            if (variablesMap.containsKey(scope) && variablesMap.get(scope).containsKey(name)) {
                return scope;
            }
        }
        return 0;
    }

    /**
     * Checks if a variable is constant.
     *
     * @param name the name of the variable
     * @return {@code true} if the variable is constant; {@code false} otherwise
     */
    public boolean isConstant(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).isConstant;
    }

    /**
     * Finds the scope in which a variable is declared.
     *
     * @param name the name of the variable
     * @return the scope number where the variable is declared
     * @throws IllegalArgumentException if the variable is not declared in any accessible scope
     */
    public int findVariableScope(String name) {
        // Helper function to find the scope where a variable is declared
        for (int scope = currentScope; scope >= 1; scope--) {
            if (variablesMap.containsKey(scope) && variablesMap.get(scope).containsKey(name)) {
                return scope;
            }
        }
        throw new IllegalArgumentException(String.format(NOT_DECLARED_IN_ANY_ACCESSIBLE_SCOPE, name));
    }
}
