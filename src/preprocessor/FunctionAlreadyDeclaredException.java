package preprocessor;

/**
 * Thrown to indicate that a function with the same name has already been declared.
 * This exception is typically used to prevent duplicate function declarations.
 */
public class FunctionAlreadyDeclaredException extends Exception {
    private static final String EXCEPTION_MESSAGE = "FunctionAlreadyDeclaredException: 2 " +
            "methods with the same name: %s";
    /**
     * function name
     */
    private final String functionName;

    /**
     * Constructs a new {@code FunctionAlreadyDeclaredException} with the specified
     * function name that caused the exception.
     *
     * @param functionName the name of the function that was declared more than once
     */
    public FunctionAlreadyDeclaredException(String functionName) {
        this.functionName = functionName;
    }
    /**
     * Returns the detail message of this exception.
     * The message includes the name of the function that was declared more than once.
     *
     * @return a formatted message indicating the duplicate function declaration
     */
    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, functionName);
    }
}
