import java.util.*;

public class SymbolTable {
    // Outer map: scope number -> (variable name -> (type, value, is constant))
    private final HashMap<Integer, HashMap<String, Triple>> variablesMap = new HashMap<>();
    private int currentScope = 0;

    static class Triple {
        private final Stack<String> globalVariableValuesStack;
        private int lastUpdatedScope;
        String type;
        String value;
        boolean isConstant;

        Triple(String type, String value, boolean isConstant) {
            this.type = type;
            this.value = value;
            this.isConstant = isConstant;
            this.globalVariableValuesStack = new Stack<>();
            this.lastUpdatedScope = 0;
        }

        @Override
        public String toString() {
            return "(" + type + ", " + value + ")";
        }
    }

    public void enterScope() {
        currentScope++;
        variablesMap.put(currentScope, new HashMap<>());
        printSymbolTable();
    }

    public int getCurrentScope() {
        return currentScope;
    }



    public void exitScope() {
        printSymbolTable();
        variablesMap.remove(currentScope);
        currentScope--;
        HashMap<String, Triple> globalVariables = variablesMap.get(1);
        if (globalVariables != null) {
            for (Map.Entry<String, Triple> entry : globalVariables.entrySet()) {
//                System.out.println(entry.getValue().globalVariableValuesStack);
                if (entry.getValue().lastUpdatedScope == getCurrentScope()) {
//                    entry.getValue().globalVariableValuesStack.pop();
                    entry.getValue().lastUpdatedScope--;
//                    System.out.println(entry.getValue().globalVariableValuesStack);
                    entry.getValue().value = entry.getValue().globalVariableValuesStack.peek();
                }
            }
        }

    }

    public void declareVariable(String name, String type, String value, boolean isConstant, boolean isParameter)
            throws VariableAlreadyDeclaredException, ConstantNonAssignmentException {
        HashMap<String, Triple> currentScopeMap = variablesMap.get(currentScope);

        if (currentScopeMap.containsKey(name)) {
            throw new VariableAlreadyDeclaredException(name);
        }
        if (isConstant && value == null && !isParameter) {
            throw new ConstantNonAssignmentException(name);
        }

        currentScopeMap.put(name, new Triple(type, value, isConstant));
        if (getCurrentScope() == 1) {
            variablesMap.get(1).get(name).globalVariableValuesStack.push(value);
            variablesMap.get(1).get(name).lastUpdatedScope++;
        }
    }

    public void assignValue(String name, String value) throws ConstantAssignmentException, UninitializedGlobalVariableException {
        int scope = findVariableScope(name); // might throw if not assigned
        if (scope == 1 && getCurrentScope() != 1 && getValue(name) == null) {
            throw new UninitializedGlobalVariableException(name);
        }
        if (variablesMap.get(scope).get(name).isConstant) {
            throw new ConstantAssignmentException(name);
        }
        Triple Triple = variablesMap.get(scope).get(name);
        Triple.value = value;
        if (getCurrentScope() == 1) {

            variablesMap.get(1).get(name).globalVariableValuesStack.push(value);
            variablesMap.get(1).get(name).lastUpdatedScope++;

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
        throw new IllegalArgumentException("Variable '" + name + "' not declared in any accessible scope");
    }
    public void printSymbolTable() {
        System.out.println("Symbol Table:");
        for (int scope : variablesMap.keySet()) {
            System.out.println("Scope " + scope + ": " + variablesMap.get(scope));
        }

    }


    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable();
        runTests(symbolTable);
    }

    public static void runTests(SymbolTable st) {
        try {
            // Test 1: Declare a constant variable
            st.enterScope();
            st.declareVariable("x", "int", "1", true, true);
            System.out.println("Declared constant x in scope 1");

            // Test 2: Attempt reassignment to a constant variable
            try {
                st.assignValue("x", "2"); // Expected to throw
            } catch (ConstantAssignmentException e) {
                System.err.println(e.getMessage()); // Ignored for testing purposes
            } catch (UninitializedGlobalVariableException e) {
                e.getMessage();
            }

            // Test 3: Declare and assign in nested scope
            st.enterScope();
            st.declareVariable("y", "String", "hello", false, true);
            System.out.println("Declared y in scope 2");
            st.assignValue("y", "world");
            System.out.println("Assigned new value to y in scope 2: " + st.getValue("y"));

            // Test 4: Shadowing variable in a nested scope
            st.declareVariable("x", "int", null, false, true);
            System.out.println("Declared x in scope 2 (shadowed)");
//            st.assignValue("x", 20);
            System.out.println("Assigned new value to x in scope 2: " + st.getValue("x"));

            // Test 5: Exit scope and access variable from parent scope
            st.exitScope();
            System.out.println("Exited scope 2");
            System.out.println("Value of x in scope 1: " + st.getValue("x"));

        } catch (Exception e) {
            // Catch-all for unexpected issues
            System.err.println("Test encountered an unexpected error: " + e.getMessage());
        } catch (VariableAlreadyDeclaredException | ConstantNonAssignmentException e) {
            e.getMessage();
        } catch (UninitializedGlobalVariableException e) {
            throw new RuntimeException(e);
        }
    }

}
