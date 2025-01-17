public class CallFunctionFromGlobalException extends Exception {

    private static final String  FUNCTION_NAME_PLACEHOLDER = "{functionName}";
    private static final String MESSAGE = String.format("Error: illegal call to function %s from global scope." +
            " You can call a function only from other function", FUNCTION_NAME_PLACEHOLDER);
    private final String functionName;

    public CallFunctionFromGlobalException(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String getMessage() {
        return String.format(MESSAGE, functionName);
    }
}
