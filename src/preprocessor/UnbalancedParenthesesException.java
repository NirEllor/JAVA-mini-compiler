package preprocessor;

/**
 * Thrown to indicate that a file contains unbalanced parentheses.
 * This exception is typically used to enforce proper syntax in files
 * that require balanced parentheses.
 */
public class UnbalancedParenthesesException extends Exception {

    private static final String EXCEPTION_MESSAGE = "UnbalancedParenthesesException: File have unbalanced parentheses";

    /**
     * Returns the detail message of this exception.
     * The message indicates that the file contains unbalanced parentheses.
     *
     * @return a message describing the unbalanced parentheses issue
     */
    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE;
    }
}
