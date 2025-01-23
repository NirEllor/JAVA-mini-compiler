package ex5.main.engine;

public class InvalidValueTypeException extends Exception {

    private final String variableName;
    private final String type;

    public InvalidValueTypeException(String variableName, String type) {
        this.variableName = variableName;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return "InvalidValueException: variable " + variableName + " is of type " + type;
    }
}
