package ex5.main.preprocessor;

public class InvalidFunctionParameterException extends Throwable {

    private final String line;

    public InvalidFunctionParameterException(String line) {
        this.line = line;
    }

    @Override
    public String getMessage() {
        return "Invalid function parameter declaration in line: " + line;
    }
}
