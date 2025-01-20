public class EmptyConditionException extends Exception {

    private static final String MESSAGE = "Error: Empty condition in {block} block";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    private final String blockType;


    public EmptyConditionException(String blockType) {
        this.blockType = blockType;
    }

    @Override
    public String getMessage(){
        return MESSAGE.replace(BLOCK_PLACEHOLDER, blockType);
    }
}
