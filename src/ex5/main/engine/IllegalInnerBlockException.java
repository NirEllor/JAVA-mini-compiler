package ex5.main.engine;

/**
 * Exception class for illegal command in a block
 */
public class IllegalInnerBlockException extends Exception {

    // Field
    private static final String MESSAGE = "IllegalInnerBlockException: There is an illegal command in a block";

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
