package engine;

/**
 * Exception class for non-existing function
 */
public class NonExistingFunctionException extends Exception {

    // Fields
    /**
     * function name
     */
    private final String functionName;
    private static final String MESSAGE1 = "NonExistingFunctionException: function ";
    private static final String MESSAGE2 = " doesnt exist";

    /**
     * Constructor - Creates a InvalidVariableName exception
     * @param functionName : String - The functions name
     */
    public NonExistingFunctionException(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE1 + functionName + MESSAGE2;
    }

}
