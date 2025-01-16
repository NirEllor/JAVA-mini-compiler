public class InvalidIntValueException extends Exception {

    private final String variableName;
    private final String currentToken;

    public InvalidIntValueException(String variableName, String currentToken) {
        this.variableName = variableName;
        this.currentToken = currentToken;
    }

    @Override
    public String getMessage() {
        return "Invalid value for variable " + variableName + ": " + currentToken;
    }
}
