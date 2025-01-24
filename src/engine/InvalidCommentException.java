package engine;

/**
 * Exception class for Invalid type of comment
 */
public class InvalidCommentException extends Exception {

    private static final String MESSAGE = "InvalidCommentException: Invalid type of comment";

    public InvalidCommentException() {
        super();
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
