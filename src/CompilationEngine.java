import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CompilationEngine {

    public static final String VOID = "void";
    private Tokenizer tokenizer;
    private SymbolTable variablesTable;
    private FunctionsTable functionsTable;
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            "int", "char", "boolean", "double", "String"));

    public CompilationEngine(String path, FunctionsTable functionsTable) throws IOException {
        try(FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            String[] listOfLines = bufferedReader.lines().toArray(String[]::new);
            this.tokenizer = new Tokenizer(listOfLines);
            this.variablesTable = new SymbolTable();
            this.functionsTable = functionsTable;
            tokenizer.advance();
            verifyFile();
            System.out.println("0");
        } catch (IOException e){
            System.out.println("2");
            System.out.println(e.getMessage());
        } catch (GlobalScopeException e) {
            System.out.println("1");
            System.err.println(e.getMessage());
        }

    }

    private void verifyFile() throws GlobalScopeException {
        String token = tokenizer.getCurrentToken();
        variablesTable.enterScope();
        int currentScope;
        while (token != null) {
            currentScope = variablesTable.getCurrentScope();
            if (TYPES.contains(token)) {
                verifyVariableDeclaration();
            } else if (token.equals(VOID)) {
                verifyFunctionDeclaration();
            } else if (functionsTable.hasFunction(token)) {
                //TODO - check if relevant
                verifyFunctionCall();
            } else if ( currentScope == 1 &&
                    variablesTable.getVariablesMap().get(currentScope).containsKey(token) ) {
                verifyVariableAssignment();
            } else {
                throw new GlobalScopeException();
            }
            token = tokenizer.getCurrentToken();
        }
    }

}
