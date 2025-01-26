package preprocessor;

/**
 * Thrown to indicate that an invalid function parameter declaration
 * was encountered in the specified line.
 * This exception is typically used to enforce correct parameter declaration syntax.
 */
    public class InvalidFunctionParameterException extends Exception {

    private static final String EXCEPTION_MESSAGE = "InvalidFunctionParameterException: Invalid function " +
            "parameter declaration in line: %s";
    /**
     * The line
     */
    private final String line;
    /**
     * Constructs a new {@code InvalidFunctionParameterException} with the specified line
     * that caused the exception.
     *
     * @param line the line of text containing the invalid function parameter declaration
     */
    public InvalidFunctionParameterException(String line) {
        this.line = line;
    }

    /**
     * Returns the detail message of this exception.
     * The message includes the offending line and describes the parameter declaration issue.
     *
     * @return a formatted message describing the invalid parameter declaration
     */
    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, line);
    }
}
