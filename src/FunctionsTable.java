import java.util.HashMap;

public class FunctionsTable {
    private final HashMap<String, HashMap<Integer, String>> functionsMap;
    // {name : {index of parameter : type}}

    public FunctionsTable() {
        functionsMap = new HashMap<>();
    }

}
