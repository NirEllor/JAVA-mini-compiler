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
        for (int scope = currentScope; scope >= 0; scope--) {
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
            st.declareVariable("x", "int", 5);
            st.declareVariable("y", "String", "Hello");
            st.declareVariable("z", "double", 3.0);
            st.declareVariable("c", "char", '!');
            st.declareVariable("d", "char", "!");
            st.declareVariable("flag", "boolean", "true");

            st.enterScope();
            st.assignValue("x", 20);
            st.assignValue("y", 30);
            st.assignValue("z", 0.0005);
            st.declareVariable("d","char", '!');
            st.assignValue("flag", false);
            System.out.println(st.getType("x"));
            st.getType("y");
            st.getType("z");
            st.getType("c");
            st.getType("d");
            st.getValue("x");
            st.getValue("y");
            st.getValue("z");
            st.getValue("c");
            st.getValue("d");
            System.out.println(st.getType("flag"));

            st.enterScope();
            st.declareVariable("x", "int", 5);
            st.assignValue("x", 6);
            st.printSymbolTable();

            st.exitScope();
            st.printSymbolTable();

            st.exitScope();
            st.printSymbolTable();
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }
}
