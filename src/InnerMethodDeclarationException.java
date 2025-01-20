public class InnerMethodDeclarationException extends Exception {

    private static final String MESSAGE = "Error: It is illegal to declare a method inside a method";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
