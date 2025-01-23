package ex5.main.engine;

public class InvalidVariableAssignmentException extends Throwable {
    private final String variableName;

    public InvalidVariableAssignmentException(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String getMessage() {
        return variableName + " was not assigned properly";
    }
}
