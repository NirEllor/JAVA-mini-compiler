public class NonExistingFunctionException extends Throwable {

    private static final String  FUNCTION_NAME_PLACEHOLDER = "{functionName}";
    private static final String MESSAGE = String.format("Error: function %s doesnt exist", FUNCTION_NAME_PLACEHOLDER);
    private final String functionName;

    public NonExistingFunctionException(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String getMessage() {
        return MESSAGE.replace(FUNCTION_NAME_PLACEHOLDER, functionName);
    }

}
