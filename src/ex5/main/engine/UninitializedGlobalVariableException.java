package ex5.main.engine;

/**
 * Exception class for uninitialized global variable assignment from an inner scope
 */
public class UninitializedGlobalVariableException extends Exception {
    public static final String MESSAGE = "UninitializedGlobalVariableException: Uninitialized global variable '%s'";
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
