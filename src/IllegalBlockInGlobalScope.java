public class IllegalBlockInGlobalScope extends Throwable {

    private static final String PLACE_PLACEHOLDER = "{type}";
    private static final String MESSAGE = "Error: {type} block can appear only in a function, " +
            "but was detected in the global scope";
    private final String blockType;

    public IllegalBlockInGlobalScope(String blockType) {
        this.blockType = blockType;
    }

    @Override
    public String getMessage() {
        return MESSAGE.replace(PLACE_PLACEHOLDER, blockType);
    }
}
