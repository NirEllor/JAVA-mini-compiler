package engine;

/**
 * Exception class for invalid assignment of a variable
 */
public class InvalidVariableAssignmentException extends Throwable {

    // Field
    private final String variableName;

    /**
     * Constructor - Creates a InvalidValueTypeException exception
     * @param variableName : String - The variable name
     */
    public InvalidVariableAssignmentException(String variableName) {
        this.variableName = variableName;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return variableName + " was not assigned properly";
    }
}
