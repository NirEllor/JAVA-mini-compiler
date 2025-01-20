public class InvalidVariableDeclarationException extends Exception {
    final String variableName;
    private final String currentToken;

    public InvalidVariableDeclarationException(String variableName, String currentToken) {
        this.variableName = variableName;
        this.currentToken = currentToken;
    }

    @Override
    public String getMessage() {
        return "InvalidVariableDeclarationException: Unexpected token '" + currentToken + "' after variable: " + variableName;
    }
}
