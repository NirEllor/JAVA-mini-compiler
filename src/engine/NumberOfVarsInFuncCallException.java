package engine;

/**
 * Exception class for illegal number of variables in function call
 */
public class NumberOfVarsInFuncCallException extends Exception {

    // Fields
    private static final String CASE_PLACEHOLDER = "{caseOfError}";
    private static final String  FUNCTION_NAME_PLACEHOLDER = "{functionName}";
    private static final String MESSAGE = String.format("NumberOfVarsInFuncCallException: " +
            "There are %s variables than" +
            " needed in the call for %s function", CASE_PLACEHOLDER, FUNCTION_NAME_PLACEHOLDER);
    private final String caseOfError;
    private final String functionName;

    /**
     * Constructor - Creates a NumberOfVarsInFuncCallException exception
     * @param caseOfError : String - more/fewer
     * @param functionName : String - the name of the function
     */
    public NumberOfVarsInFuncCallException(String caseOfError, String functionName) {
        this.caseOfError = caseOfError;
        this.functionName = functionName;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        String res = MESSAGE.replace(CASE_PLACEHOLDER, caseOfError);
        return res.replace(FUNCTION_NAME_PLACEHOLDER, functionName);
    }
}
