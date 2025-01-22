public class UninitializedGlobalVariableException extends Throwable {
    private final String name;

    public UninitializedGlobalVariableException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return String.format("Uninitialized global variable '%s'", name);
    }
}
