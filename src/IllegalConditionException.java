public class IllegalConditionException extends Exception {

    private static final String MESSAGE = "Illegal condition in {block} block. Condition is a boolean value" +
            " that is either: One of the reserved words is true or false / An initialized boolean, double or" +
            " int variable / A double or int constant/value";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    private final String blockType;


    public IllegalConditionException(String blockType) {
        this.blockType = blockType;
    }

    @Override
    public String getMessage(){
        return MESSAGE.replace(BLOCK_PLACEHOLDER, blockType);
    }
}
