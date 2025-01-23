package ex5.main.engine;

public class VariableAlreadyDeclaredException extends Throwable {
    private final String name;

    public VariableAlreadyDeclaredException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Variable '" + name + "' already declared in the current scope";
    }
}
