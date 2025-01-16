public class ConstantAssignmentException extends Exception {
    private final String name;
    public ConstantAssignmentException(String name) {
        this.name = name;

    }

    @Override
    public String getMessage() {
        return String.format("%s is constant, thus cannot be assigned", name);
    }
}
