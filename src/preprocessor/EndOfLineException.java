package preprocessor;

/**
 * Thrown to indicate that a line does not end with one of the required
 * characters (`;`, `{`, or `}`), violating the expected syntax rules.
 */
public class EndOfLineException extends Exception {
    private static final String EXCEPTION_MESSAGE = "EndOfLineException: End of line does not end with " +
            "';', '{' or '}': %s ";
    private final String line;

    /**
     * Constructs a new {@code EndOfLineException} with the specified line
     * that caused the exception.
     *
     * @param line the line of text that does not conform to the expected end-of-line syntax
     */
    public EndOfLineException(String line) {
        this.line = line;
    }
    /**
     * Returns the detail message of this exception.
     * The message includes the offending line and explains the specific syntax issue.
     *
     * @return a formatted message describing the syntax violation
     */
    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE,line);
    }
}
