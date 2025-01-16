public class FunctionException extends Exception {
    private final String functionName;

    public FunctionException(String functionName) {
        this.functionName = functionName;
    }
    @Override
    public String getMessage() {
        return String.format("Function %s already declared", functionName);
    }
}
