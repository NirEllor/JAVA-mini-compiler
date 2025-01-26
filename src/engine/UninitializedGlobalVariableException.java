package engine;

/**
 * Exception class for uninitialized global variable assignment from an inner scope
 */
public class UninitializedGlobalVariableException extends Exception {
    private static final String MESSAGE = "UninitializedGlobalVariableException: " +
            "Uninitialized global variable '%s'";
    /**
     * global variable name
     */
    private final String name;

    /**
     * Constructor - Creates a UninitializedGlobalVariableException exception
     * @param name : String - the name of the variable
     */
    public UninitializedGlobalVariableException(String name) {
        this.name = name;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return String.format(MESSAGE, name);
    }
}
