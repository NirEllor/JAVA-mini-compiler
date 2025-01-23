package ex5.main.engine;

/**
 * Exception class for Invalid line format of double (or more) assignment/declaration
 * in one line (For example: int a=5, String s;)
 */
public class InvalidLineFormat extends Throwable {

    public static final String MESSAGE = "InvalidLineFormat: each line can have only one assignment/declaration" +
            " variables of specific type, with only one ;";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
