package engine;

/**
 * Exception class for non-existing variable
 */
public class NonExistingVariableException extends Exception {

    // Fields
    private static final String PLACE_PLACEHOLDER = "{name}";
    private static final String MESSAGE = "NonExistingVariableException: variable {name} doesnt exist" +
            "in this scope or earlier";
    private final String name;

    /**
     * Constructor - Creates a NonExistingVariableException exception
     * @param name : String - The name of the variable
     */
    public NonExistingVariableException(String name) {
        this.name = name;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE.replace(PLACE_PLACEHOLDER, name);
    }
}
