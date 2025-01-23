package ex5.main.engine;

public class IllegalReturnFormat extends Exception {

    private static final String MESSAGE = "Error: illegal return format. Must be written like : return;";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
