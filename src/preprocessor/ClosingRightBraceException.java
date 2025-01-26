package preprocessor;

/**
 * Thrown to indicate that a line contains a closing right brace ('}')
 * that should be on a separate line, according to the formatting rules.
 */
public class ClosingRightBraceException extends Exception {
    private static final String EXCEPTION_MESSAGE = "ClosingRightBraceException: The line \" %s \" has a " +
            "rightBrace {' which should be in " +
            "a separate line";
    private final String line;

    /**
     * Constructs a new {@code ClosingRightBraceException} with the specified line
     * containing the improperly placed right brace.
     *
     * @param line the line of text that caused the exception
     */
    public ClosingRightBraceException(String line) {
        this.line = line;
    }

    /**
     * Returns the detail message of this exception.
     * The message includes the offending line and describes the formatting issue.
     *
     * @return a message indicating the formatting error with the line
     */
    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, line);
    }
}
