package preprocessor;

public class UnbalancedParenthesesException extends Exception {

    public UnbalancedParenthesesException() {
//        super();
    }

    @Override
    public String getMessage() {
        return "File have unbalanced parentheses";
    }
}
