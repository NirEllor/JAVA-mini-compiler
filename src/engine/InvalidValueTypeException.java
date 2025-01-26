package engine;

/**
 * Exception class for invalid value type of variable
 */
public class InvalidValueTypeException extends Exception {


    private static final String IS_OF_TYPE = " is of type ";
    private static final String MESSAGE = "InvalidValueTypeException: variable ";
    // Fields
    /**
     * variable name
     */
    private final String variableName;
    /**
     * value type
     */
    private final String type;

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
        return MESSAGE + variableName + IS_OF_TYPE + type;
    }
}
