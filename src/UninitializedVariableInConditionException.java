public class UninitializedVariableInConditionException extends Exception {

    private static final String MESSAGE = "variable {var} is uninitialized in the {block} condition. ";
    private static final String VAR_PLACEHOLDER = "{var}";
    private static final String BLOCK_PLACEHOLDER = "{block}";
    private final String var;
    private final String blockType;


    public UninitializedVariableInConditionException(String var, String blockType) {
        this.var = var;
        this.blockType = blockType;
    }

    @Override
    public String getMessage(){
        String res = MESSAGE.replace(VAR_PLACEHOLDER, var);
        return res.replace(BLOCK_PLACEHOLDER, blockType);
    }
}
