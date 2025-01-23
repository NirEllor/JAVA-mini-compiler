package ex5.main.engine;

public class IllegalInnerBlockException extends Throwable {

    public static final String MESSAGE = "IllegalInnerBlockException: There is an illegal command in a block";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
