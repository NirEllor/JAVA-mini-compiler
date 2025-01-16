public class InvalidVariableDeclarationException extends Exception {
    final String variableName;
    public InvalidVariableDeclarationException(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String getMessage() {
        return "The variable " + variableName + " declaration is invalid";
    }
}
