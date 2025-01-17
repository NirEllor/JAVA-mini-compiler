public class FinalReturnException extends Exception {

    private static final String MESSAGE = "Error: missing final return statement";

    @Override
    public String getMessage() {
        return MESSAGE;
    }

}
