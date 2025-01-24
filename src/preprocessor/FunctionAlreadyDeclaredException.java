package preprocessor;

public class FunctionAlreadyDeclaredException extends Exception {
    private final String functionName;

    public FunctionAlreadyDeclaredException(String functionName) {
        this.functionName = functionName;
    }
    @Override
    public String getMessage() {
        return String.format("2 methods with the same name: %s", functionName);
    }
}
