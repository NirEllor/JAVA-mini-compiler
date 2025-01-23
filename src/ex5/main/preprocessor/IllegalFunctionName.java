package ex5.main.preprocessor;

public class IllegalFunctionName extends Throwable {

    public static final String MESSAGE = "IllegalFunctionName: illegal function name of function: ";
    private final String functionName;

    public IllegalFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String getMessage() {
        return MESSAGE + functionName;
    }
}
