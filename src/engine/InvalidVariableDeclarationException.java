package engine;

/**
 * Exception class for unexpected token after variable
 */
public class InvalidVariableDeclarationException extends Exception {

    // Fields
    private static final String MESSAGE = "InvalidVariableDeclarationException: Unexpected token '";
    private final String variableName;
    private final String currentToken;

    /**
     * Constructor - Creates a InvalidVariableDeclarationException exception
     * @param variableName : String - The variable name
     * @param currentToken : String - the token after the variable
     */
    public InvalidVariableDeclarationException(String variableName, String currentToken) {
        this.variableName = variableName;
        this.currentToken = currentToken;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE + currentToken + "' after variable: " + variableName;
    }
}
