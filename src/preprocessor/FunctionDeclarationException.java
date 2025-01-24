package preprocessor;

public class FunctionDeclarationException extends Throwable {

    private final String line;

    public FunctionDeclarationException(String line) {
        this.line = line;
    }

    @Override
    public String getMessage() {
        return "FunctionDeclarationException: illegal function declaration in line: " + line;
    }
}
