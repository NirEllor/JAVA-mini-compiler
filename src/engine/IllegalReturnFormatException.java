package engine;

/**
 * Exception class for illegal return format
 */
public class IllegalReturnFormatException extends Exception {

    // Fields
    private static final String MESSAGE = "IllegalReturnFormatException: illegal return format. Must be written like : return;";

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
