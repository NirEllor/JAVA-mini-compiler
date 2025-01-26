package engine;

/**
 * Exception class illegal block in global scope
 */
public class IllegalBlockInGlobalScopeException extends Exception {

    // Fields
    private static final String PLACE_PLACEHOLDER = "{type}";
    private static final String MESSAGE = "IllegalBlockInGlobalScopeException: {type} block can appear " +
            "only in a function, " +
            "but was detected in the global scope";
    private final String blockType;

    /**
     * Constructor - Creates a IllegalBlockInGlobalScopeException exception
     * @param blockType : String - The blocks type
     */
    public IllegalBlockInGlobalScopeException(String blockType) {
        this.blockType = blockType;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE.replace(PLACE_PLACEHOLDER, blockType);
    }
}
