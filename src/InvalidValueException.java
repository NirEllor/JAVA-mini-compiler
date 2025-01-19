public class InvalidValueException extends Exception {

    private final String variableName;
    private final String currentToken;
    private final String type;

    public InvalidValueException(String variableName, String currentToken, String type) {
        this.variableName = variableName;
        this.currentToken = currentToken;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return "InvalidValueException: Invalid value for variable " + variableName + " of type " + type + ": " + currentToken;
    }
}
