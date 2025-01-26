package engine;

/**
 * Exception class for empty condition in if/while
 */
public class EmptyConditionException extends Exception {

    // Fields
    private static final String MESSAGE = "EmptyConditionException: Empty condition in {block} block";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    /**
     * block type
     */
    private final String blockType;

    /**
     * Constructor - Creates a EmptyConditionException exception
     * @param blockType : String - The blocks type
     */
    public EmptyConditionException(String blockType) {
        this.blockType = blockType;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage(){
        return MESSAGE.replace(BLOCK_PLACEHOLDER, blockType);
    }
}
