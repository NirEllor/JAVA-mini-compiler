public class IllegalVarTypeInConditionException extends Exception {

    private static final String MESSAGE = "{type} is an illegal variable type for condition of a {block}";
    private static final String TYPE_PLACEHOLDER = "{type}";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    private final String varType;
    private final String blockType;

    public IllegalVarTypeInConditionException(String varType, String blockType) {
        this.varType = varType;
        this.blockType = blockType;
    }

    @Override
    public String getMessage(){
        String res = MESSAGE.replace(TYPE_PLACEHOLDER, varType);
        return res.replace(BLOCK_PLACEHOLDER, blockType);
    }
}
