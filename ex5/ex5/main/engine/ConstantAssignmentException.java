package ex5.main.engine;

/**
 * Exception class for try to assign a constant variable
 */
public class ConstantAssignmentException extends Exception {

    public static final String MESSAGE = "ConstantAssignmentException: %s is constant, thus cannot be assigned";
    // fields
    private final String name;

    /**
     * Constructor - Creates a ConstantAssignmentException exception
     * @param name : String - The variable name
     */
    public ConstantAssignmentException(String name) {
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
