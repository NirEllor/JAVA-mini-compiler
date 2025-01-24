package ex5.main.engine;

/**
 * Exception class for invalid value type of variable
 */
public class InvalidValueTypeException extends Exception {


    // Fields
    private final String variableName;
    private final String type;
    private static final String MESSAGE = "InvalidValueTypeException: variable ";

    /**
     * Constructor - Creates a InvalidValueTypeException exception
     * @param variableName : String - The variable name
     * @param type : String - the type of the value of the variable
     */
    public InvalidValueTypeException(String variableName, String type) {
        this.variableName = variableName;
        this.type = type;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE + variableName + " is of type " + type;
    }
}
