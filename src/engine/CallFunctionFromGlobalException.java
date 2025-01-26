package engine;

/**
 * Exception class for calling a function from the global scope
 */
public class CallFunctionFromGlobalException extends Exception {

    // Fields
    private static final String  FUNCTION_NAME_PLACEHOLDER = "{functionName}";
    private static final String MESSAGE = String.format("CallFunctionFromGlobalException: illegal " +
            "call to function" +
            " %s from global scope. You can call a function only from other function",
            FUNCTION_NAME_PLACEHOLDER);
    private final String functionName;

    /**
     * Constructor - Creates a CallFunctionFromGlobalException exception
     * @param functionName : String - The function name
     */
    public CallFunctionFromGlobalException(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return String.format(MESSAGE, functionName);
    }
}
