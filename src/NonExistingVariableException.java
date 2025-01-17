public class NonExistingVariableException extends Throwable {

    private static final String PLACE_PLACEHOLDER = "{type}";
    private static final String MESSAGE = "Error: variable {type} doesnt exist in this scope or earlier";
    private final String var;

    public NonExistingVariableException(String var) {
        this.var = var;
    }

    @Override
    public String getMessage() {
        return MESSAGE.replace(PLACE_PLACEHOLDER, var
        );
    }
}
