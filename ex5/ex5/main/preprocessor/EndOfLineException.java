package ex5.main.preprocessor;

public class EndOfLineException extends Exception {
    private final String line;

    public EndOfLineException(String line) {
//        super();
        this.line = line;
    }

    @Override
    public String getMessage() {
        return String.format("End of line does not end with ';', '{' or '}': %s ",line);
    }
}
