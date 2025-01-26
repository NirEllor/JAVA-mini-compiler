package engine;

/**
 * Exception class for uninitialized variable in block condition
 */
public class UninitializedVariableInConditionException extends Exception {

    // Fields
    private static final String MESSAGE = "UninitializedVariableInConditionException: " +
            "variable {var} is uninitialized" +
            " in the {block} condition. ";
    private static final String VAR_PLACEHOLDER = "{var}";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    private final String name;
    private final String blockType;

    /**
     * Constructor - Creates a UninitializedVariableInConditionException exception
     * @param name : String - the name of the variable
     * @param blockType: String - if/while
     */
    public UninitializedVariableInConditionException(String name, String blockType) {
        this.name = name;
        this.blockType = blockType;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage(){
        String res = MESSAGE.replace(VAR_PLACEHOLDER, name);
        return res.replace(BLOCK_PLACEHOLDER, blockType);
    }
}
