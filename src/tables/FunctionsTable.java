package tables;

import preprocessor.FunctionAlreadyDeclaredException;
import java.util.HashMap;
import java.util.List;

/**
 * Manages a collection of functions and their associated parameters.
 * Each function is stored with its name and a mapping of parameter indices to their types.
 */
public class FunctionsTable {
    // Outer map: function name -> (parameter index -> parameter type)
    private final HashMap<String, HashMap<Integer, String>> functionsTable = new HashMap<>();

    /**
     * Adds a new function to the table with its parameters and types.
     *
     * @param functionName   the name of the function
     * @param parameterTypes a list of parameter types in the order they appear in the function
     * @throws FunctionAlreadyDeclaredException if a function with the same name is already declared
     */
    public void addFunction(String functionName, List<String> parameterTypes) throws
            FunctionAlreadyDeclaredException {
        // Add a new function with its parameters and types
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

    /**
     * Checks if a function with the specified name exists in the table.
     *
     * @param functionName the name of the function to check
     * @return {@code true} if the function exists, {@code false} otherwise
     */
    public boolean hasFunction(String functionName) {
        // Check if a function exists
        return functionsTable.containsKey(functionName);
    }

    /**
     * Retrieves the parameter mapping of a function by its name.
     * The mapping associates parameter indices with their respective types.
     *
     * @param functionName the name of the function
     * @return a map where the keys are parameter indices and the values are parameter types,
     *         or {@code null} if the function does not exist
     */
    public HashMap<Integer, String> getFunctionVariables(String functionName){
        return functionsTable.get(functionName);
    }
}
