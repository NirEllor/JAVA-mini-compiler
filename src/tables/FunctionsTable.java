package tables;

import preprocessor.FunctionAlreadyDeclaredException;
import java.util.HashMap;
import java.util.List;

public class FunctionsTable {
    // Outer map: function name -> (parameter index -> parameter type)
    private final HashMap<String, HashMap<Integer, String>> functionsTable = new HashMap<>();

    // Add a new function with its parameters and types
    public void addFunction(String functionName, List<String> parameterTypes) throws
            FunctionAlreadyDeclaredException {
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

    // Check if a function exists
    public boolean hasFunction(String functionName) {
        return functionsTable.containsKey(functionName);
    }


    public HashMap<Integer, String> getFunctionVariables(String functionName){
        return functionsTable.get(functionName);
    }
}
