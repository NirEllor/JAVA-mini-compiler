package engine;

/**
 * Exception class for illegal variable type for condition
 */
public class IllegalVarTypeInConditionException extends Exception {

    // Fields
    private static final String MESSAGE = "IllegalVarTypeInConditionException: {type} is an illegal " +
            "variable type" +
            " for condition of a {block}";
    private static final String TYPE_PLACEHOLDER = "{type}";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    /**
     * type of variable
     */
    private final String varType;
    private final String blockType;

    /**
     * Constructor - Creates a IllegalVarTypeInConditionException exception
     * @param blockType : String - The blocks type
     */
    public IllegalVarTypeInConditionException(String varType, String blockType) {
        this.varType = varType;
        this.blockType = blockType;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage(){
        String res = MESSAGE.replace(TYPE_PLACEHOLDER, varType);
        return res.replace(BLOCK_PLACEHOLDER, blockType);
    }
}
