package preprocessor;

/**
 * Thrown to indicate that an illegal function declaration was encountered in the specified line.
 * This exception is typically used to enforce correct function declaration syntax.
 */
public class FunctionDeclarationException extends Exception {

    private static final String EXCEPTION_MESSAGE = "FunctionDeclarationException: illegal " +
            "function declaration in line: %s";
    /**
     * The line
     */
    private final String line;

    /**
     * Constructs a new {@code FunctionDeclarationException} with the specified line
     * that caused the exception.
     *
     * @param line the line of text containing the illegal function declaration
     */
    public FunctionDeclarationException(String line) {
        this.line = line;
    }
    /**
     * Returns the detail message of this exception.
     * The message includes the offending line and provides context about the illegal declaration.
     *
     * @return a formatted message describing the illegal function declaration
     */
    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, line);
    }
}
