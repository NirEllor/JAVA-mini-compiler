package ex5.main.engine;

public class InvalidVariableDeclarationException extends Exception {
    public static final String MESSAGE = "InvalidVariableDeclarationException: Unexpected token '";
    final String variableName;
    private final String currentToken;

    public InvalidVariableDeclarationException(String variableName, String currentToken) {
        this.variableName = variableName;
        this.currentToken = currentToken;
    }

    @Override
    public String getMessage() {
        return MESSAGE + currentToken + "' after variable: " + variableName;
    }
}
