import java.util.Stack;

// Generic Pair class for storing variable name and a stack of values
class Pair<V> {
    private final String variableName;
    private final Stack<V> second;

    // Constructor to initialize the variable name and an empty stack
    public Pair(String variableName) {
        this.variableName = variableName;
        this.second = new Stack<>();
    }

    public String getFirst() {
        return variableName;
    }

    public Stack<V> getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
