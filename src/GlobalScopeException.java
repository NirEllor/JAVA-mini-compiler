public class GlobalScopeException extends Throwable {
    public GlobalScopeException() {
        super();
    }

    @Override
    public String getMessage() {
        return "Problem in the global scope, check variables  and functions";
    }
}
