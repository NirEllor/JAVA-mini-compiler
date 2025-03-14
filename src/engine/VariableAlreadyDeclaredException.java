package engine;

/**
 * Exception class for variable already declared in the current scope
 */
public class VariableAlreadyDeclaredException extends Exception {

    private static final String MESSAGE1 = "VariableAlreadyDeclaredException: Variable '";
    private static final String MESSAGE2 = "' already declared in the current scope";
    // Field
    /**
     * variable name
     */
    private final String name;

    /**
     * Constructor - Creates a VariableAlreadyDeclaredException exception
     * @param name : String - the name of the variable
     */
    public VariableAlreadyDeclaredException(String name) {
        this.name = name;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE1 + name + MESSAGE2;

    }
}
