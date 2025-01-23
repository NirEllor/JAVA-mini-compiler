package ex5.main.engine;

public class InvalidVariableName extends Exception {
    final String variableName;
    public InvalidVariableName(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String getMessage() {
        return "The name '" + variableName + "' is not a valid variable name";
    }
}
