package ex5.main.engine;

public class InvalidTypeException extends Exception {
    private final String variableName;
    private final String type;
    private final String valueType;

    public InvalidTypeException(String variableName, String type, String valueType) {
        this.variableName = variableName;
        this.type = type;
        this.valueType = valueType;
    }

    @Override
    public String getMessage() {
        return variableName + " is of type " + type + " and can't be " + valueType;
    }
}
