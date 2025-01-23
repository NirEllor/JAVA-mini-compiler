package ex5.main.engine;

/**
 * Exception class for illegal command in global scope
 */
public class GlobalScopeException extends Exception {

    // Fields
    public static final String MESSAGE = "GlobalScopeException: Illegal command in the global scope, check variables  and functions";

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
