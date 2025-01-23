package ex5.main.tables;

import ex5.main.preprocessor.FunctionAlreadyDeclaredException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionsTable {
    // Outer map: function name -> (parameter index -> parameter type)
    private final HashMap<String, HashMap<Integer, String>> functionsTable = new HashMap<>();

    // Add a new function with its parameters and types
    public void addFunction(String functionName, List<String> parameterTypes) throws FunctionAlreadyDeclaredException {
        if (!hasFunction(functionName)) {
            HashMap<Integer, String> parameters = new HashMap<>();
            for (int i = 0; i < parameterTypes.size(); i++) {
                parameters.put(i, parameterTypes.get(i));
            }

            functionsTable.put(functionName, parameters);
        }
        else {
            throw new FunctionAlreadyDeclaredException(functionName);
        }

    }

    // Get the parameter type by function name and parameter index
    public String getParameterType(String functionName, int parameterIndex) {
        if (!functionsTable.containsKey(functionName)) {
            throw new IllegalArgumentException("Function '" + functionName + "' not found");
        }

        Map<Integer, String> parameters = functionsTable.get(functionName);
        if (!parameters.containsKey(parameterIndex)) {
            throw new IllegalArgumentException("Parameter index " + parameterIndex + " not found for function '" + functionName + "'");
        }

        return parameters.get(parameterIndex);
    }

    // Get the number of parameters for a function
    public int getParameterCount(String functionName) {
        if (!functionsTable.containsKey(functionName)) {
            throw new IllegalArgumentException("Function '" + functionName + "' not found");
        }

        return functionsTable.get(functionName).size();
    }

    // Check if a function exists
    public boolean hasFunction(String functionName) {
        return functionsTable.containsKey(functionName);
    }

    // Print the functions table for debugging
    public void printFunctionsTable() {
        System.out.println("Functions Table:");
        for (String functionName : functionsTable.keySet()) {
            System.out.print("Function '" + functionName + "' -> Parameters: ");
            Map<Integer, String> parameters = functionsTable.get(functionName);
            for (int index : parameters.keySet()) {
                System.out.print("[" + index + ": " + parameters.get(index) + "] ");
            }
            System.out.println();
        }
    }



    public HashMap<Integer, String> getFunctionVariables(String functionName){

        return functionsTable.get(functionName);

    }

    public static void main(String[] args) throws FunctionAlreadyDeclaredException {
        FunctionsTable ft = new FunctionsTable();

        // Test 1: Add functions and print table
        ft.addFunction("sum", Arrays.asList("int", "int"));
        ft.addFunction("concat", Arrays.asList("String", "String"));

        ft.printFunctionsTable();

        // Test 2: Get parameter types and counts
        System.out.println("sum param 0 type: " + ft.getParameterType("sum", 0));  // Output: int
        System.out.println("concat param count: " + ft.getParameterCount("concat"));  // Output: 2

        // Test 3: Check for existing functions
        System.out.println("Has 'sum': " + ft.hasFunction("sum"));  // Output: true
        System.out.println("Has 'divide': " + ft.hasFunction("divide"));  // Output: false

        // Test 4: Add a function with the same name (should throw an exception)
        try {
            ft.addFunction("sum", Arrays.asList("int", "int"));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());  // Output: Function 'sum' already exists
        }
    }
}
