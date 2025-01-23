package ex5.main.engine;

/**
 * Exception class for non-siting final return
 */
public class FinalReturnException extends Exception {

    // Fields
    private static final String MESSAGE = "FinalReturnException: missing final return statement";

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE;
    }

}
