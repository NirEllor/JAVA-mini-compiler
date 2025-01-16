public class GlobalScopeException extends Exception {
    public GlobalScopeException() {
        super();
    }

    @Override
    public String getMessage() {
        return "Problem in the global scope, check variables  and functions";
    }
}
