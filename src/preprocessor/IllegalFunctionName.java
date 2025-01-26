package preprocessor;

/**
 * Thrown to indicate that a function name is illegal or does not comply
 * with the expected naming conventions or rules.
 */
public class IllegalFunctionName extends Exception {

    private static final String MESSAGE = "IllegalFunctionName: illegal function name of function: ";
    private final String functionName;
    /**
     * Constructs a new {@code IllegalFunctionName} with the specified function name
     * that caused the exception.
     *
     * @param functionName the name of the function that is deemed illegal
     */
    public IllegalFunctionName(String functionName) {
        this.functionName = functionName;
    }
    /**
     * Returns the detail message of this exception.
     * The message includes the offending function name and describes the error.
     *
     * @return a formatted message indicating the illegal function name
     */
    @Override
    public String getMessage() {
        return MESSAGE + functionName;
    }
}
