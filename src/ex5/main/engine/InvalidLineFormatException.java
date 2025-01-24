package ex5.main.engine;

/**
 * Exception class for Invalid line format of double (or more) assignment/declaration
 * in one line (For example: int a=5, String s;)
 */
public class InvalidLineFormatException extends Exception {

    // Field
    private static final String MESSAGE = "InvalidLineFormatException: each line can have only one " +
            "assignment/declaration variables of specific type, with only one ;";

    /**
     * Returns an error message
     * @return : String - error message
     */
    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
