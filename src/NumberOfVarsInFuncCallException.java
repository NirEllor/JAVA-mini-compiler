public class NumberOfVarsInFuncCallException extends Throwable {

    private static final String CASE_PLACEHOLDER = "{caseOfError}";
    private static final String  FUNCTION_NAME_PLACEHOLDER = "{functionName}";
    private static final String MESSAGE = String.format("Error: There are %s variables than needed, in the call for" +
            " %s function", CASE_PLACEHOLDER, FUNCTION_NAME_PLACEHOLDER);
    private final String caseOfError;
    private final String functionName;

    public NumberOfVarsInFuncCallException(String caseOfError, String functionName) {
        this.caseOfError = caseOfError;
        this.functionName = functionName;
    }

    @Override
    public String getMessage() {
        String res = MESSAGE.replace(CASE_PLACEHOLDER, caseOfError);
        return res.replace(FUNCTION_NAME_PLACEHOLDER, functionName);
    }
}
