package engine;

/**
 * Exception class for try to declare a constant variable without assignment
 */
public class ConstantNonAssignmentException extends Exception {

    // Fields
    private static final String MESSAGE = "ConstantNonAssignmentException: Constant variable %s " +
            "cannot be null";
    /**
     * constant name
     */
    private final String name;

    /**
     * Constructor - Creates a ConstantNonAssignmentException exception
     * @param name : String - The variable name
     */
    public ConstantNonAssignmentException(String name) {
        this.name = name;
    }

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return String.format(MESSAGE, name);
    }
}
