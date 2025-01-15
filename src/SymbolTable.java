import java.util.*;

public class SymbolTable {
    // Outer map: scope number -> (variable name -> (type, value))
    private final HashMap<Integer, HashMap<String, Pair>> variablesMap = new HashMap<>();
    private int currentScope = 0;

    static class Pair {
        String type;
        Object value;

        Pair(String type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + type + ", " + value + ")";
        }
    }

    public void enterScope() {
        currentScope++;
        variablesMap.put(currentScope, new HashMap<>());
    }

    public void exitScope() {
        variablesMap.remove(currentScope);
        currentScope--;
    }

    public void declareVariable(String name, String type, Object value) {
        HashMap<String, Pair> currentScopeMap = variablesMap.get(currentScope);

        if (currentScopeMap.containsKey(name)) {
            throw new IllegalArgumentException("Variable '" + name + "' already declared in the current scope");
        }

        currentScopeMap.put(name, new Pair(type, value));
    }

    public void assignValue(String name, Object value) {
        int scope = findVariableScope(name);
        Pair pair = variablesMap.get(scope).get(name);
        pair.value = value;
    }

    public Object getValue(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).value;
    }

    public String getType(String name) {
        int scope = findVariableScope(name);
        return variablesMap.get(scope).get(name).type;
    }

    // Helper function to find the scope where a variable is declared
    private int findVariableScope(String name) {
        for (int scope = currentScope; scope >= 1; scope--) {
            System.out.println(scope);
            if (variablesMap.containsKey(scope) && variablesMap.get(scope).containsKey(name)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Variable '" + name + "' not declared in any accessible scope");
    }

    // Function to print the symbol table
    public void printSymbolTable() {
        System.out.println("Symbol Table:");
        for (int scope : variablesMap.keySet()) {
            System.out.println("Scope " + scope + ": " + variablesMap.get(scope));
        }
    }

    public static void main(String[] args) {
        SymbolTable st = new SymbolTable();
        runTests(st);
    }

    // Test suite for the key functions
    public static void runTests(SymbolTable st) {

        try {
            st.enterScope();
            st.declareVariable("x", "int", 1);
//            System.out.println(st.findVariableScope("x"));
            st.enterScope();
            st.declareVariable("x", "int", 1);
//            System.out.println(st.findVariableScope("x"));
//            st.exitScope();
//            System.out.println(st.findVariableScope("x"));
//            st.declareVariable("x", "float", 2);
            st.assignValue("y", 2);


        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }
}
