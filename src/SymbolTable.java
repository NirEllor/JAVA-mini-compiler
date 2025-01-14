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


//        try {
//            System.out.println("Test 1: Declare and retrieve variable");
//            st.enterScope();
//            st.declareVariable("x", "int", 5);
//            System.out.println("x = " + st.getValue("x"));  // Output: 5
//            st.printSymbolTable();
//
//            System.out.println("\nTest 2: Assign new value to variable");
//            st.assignValue("x", 10);
//            System.out.println("x = " + st.getValue("x"));  // Output: 10
//            st.printSymbolTable();
//
//            System.out.println("\nTest 3: Enter new scope and shadow variable");
//            st.enterScope();
//            st.declareVariable("y", "String", "Hello");
//            System.out.println("y = " + st.getValue("y"));  // Output: Hello
//            st.assignValue("x", 20);  // Updates x in outer scope
//            System.out.println("x = " + st.getValue("x"));  // Output: 20
//            st.printSymbolTable();
//
//            System.out.println("\nTest 4: Exit scope and check variable persistence");
//            st.exitScope();
//            System.out.println("x = " + st.getValue("x"));  // Output: 20
//            st.printSymbolTable();
//
//            System.out.println("\nTest 5: Redeclare variable in same scope (should throw exception)");
//            st.declareVariable("z", "int", 30);  // Error: already declared in scope
//        } catch (IllegalArgumentException e) {
//            System.err.println(e.getMessage());
//        }
//        System.out.println("\nAll basic tests completed.\n");
    }
}
