public class InavlidVariableName extends Exception {
    final String variableName;
    public InavlidVariableName(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String getMessage() {
        return "The name '" + variableName + "' is not a valid variable name";
    }
}
