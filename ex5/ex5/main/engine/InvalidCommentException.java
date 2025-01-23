package ex5.main.engine;

public class InvalidCommentException extends Exception {
    public InvalidCommentException() {
        super();
    }

    @Override
    public String getMessage() {
        return "Invalid type of comment";
    }
}
