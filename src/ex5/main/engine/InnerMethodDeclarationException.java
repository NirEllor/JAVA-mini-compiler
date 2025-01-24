package ex5.main.engine;

/**
 * Exception class for illegal declaration of a method inside another method
 */
public class InnerMethodDeclarationException extends Exception {

    // Fields
    private static final String MESSAGE = "InnerMethodDeclarationException: It is illegal to declare a method inside" +
            " a method";

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
