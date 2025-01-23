package ex5.main.preprocessor;

public class ClosingRightBraceException extends Throwable {
    private final String line;

    public ClosingRightBraceException(String line) {
        this.line = line;
    }

    @Override
    public String getMessage() {
        return "The line \"" +  line + "\" has a rightBrace {' which should be in" +
                "a separate line";
    }
}
