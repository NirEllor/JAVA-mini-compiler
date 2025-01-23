package ex5.main.engine;

public class MissingVariableTypeInFunctionDeclarationException extends Exception {

    private static final String  FUNCTION_NAME_PLACEHOLDER = "{functionName}";
    private static final String MESSAGE = String.format("Error: There is missing type of a variable in function %s " +
            "declaration", FUNCTION_NAME_PLACEHOLDER);
    private final String functionName;

    public MissingVariableTypeInFunctionDeclarationException(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String getMessage() {
        return MESSAGE.replace(FUNCTION_NAME_PLACEHOLDER, functionName);
    }
}
