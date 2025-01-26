package engine;

/**
 * Exception class for invalid name for a variable
 */
public class InvalidVariableNameException extends Exception {

    // Fields
    private static final String MESSAGE1 = "InvalidVariableNameException name '";
    private static final String MESSAGE2 = "' is invalid variable name";
    /**
     * variable name
     */
    private final String variableName;

    /**
     * Constructor - Creates a InvalidVariableNameException exception
     * @param variableName : String - The variable name
     */
    public InvalidVariableNameException(String variableName) {
        this.variableName = variableName;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE1 + variableName + MESSAGE2;
    }
}
