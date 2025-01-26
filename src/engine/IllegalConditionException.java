package engine;

/**
 * Exception class for illegal condition in if/while block
 */
public class IllegalConditionException extends Exception {

    // Fields
    private static final String MESSAGE = "IllegalConditionException: Illegal condition in {block} " +
            "block. Condition" +
            " is a boolean value that is either: One of the reserved words is true or false / An " +
            "initialized boolean,"
            + " double or int variable / A double or int constant/value Each one can be separated with " +
            "one \"&&\" or \"||\"";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    /**
     * block type
     */
    private final String blockType;

    /**
     * Constructor - Creates a IllegalConditionException exception
     * @param blockType : String - The blocks type
     */
    public IllegalConditionException(String blockType) {
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
