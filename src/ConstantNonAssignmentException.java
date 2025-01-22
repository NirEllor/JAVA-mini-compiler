public class ConstantNonAssignmentException extends Throwable {
    private final String name;

    public ConstantNonAssignmentException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return String.format("Constant variable %s cannot be null", name);
    }
}
