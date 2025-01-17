public class InvalidVariableAssignmentEception extends Throwable {
    private final String variableName;

    public InvalidVariableAssignmentEception(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String getMessage() {
        return variableName + " was not assigned properly";
    }
}
