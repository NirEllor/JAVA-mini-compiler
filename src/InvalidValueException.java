public class InvalidValueException extends Exception {

    private final String variableName;
    private final String type;

    public InvalidValueException(String variableName, String type) {
        this.variableName = variableName;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return "InvalidValueException: variable " + variableName + " is of type " + type;
    }
}
