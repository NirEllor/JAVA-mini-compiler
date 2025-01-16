import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CompilationEngine {

    public static final String VOID = "void";
    private Tokenizer tokenizer;
    private SymbolTable symbolTable;
    private FunctionsTable functionsTable;
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            "int", "char", "boolean", "double", "String"));

    public CompilationEngine(String path, FunctionsTable functionsTable) throws IOException {
        try(FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            String[] listOfLines = bufferedReader.lines().toArray(String[]::new);
            this.tokenizer = new Tokenizer(listOfLines);
            this.symbolTable = new SymbolTable();
            this.functionsTable = functionsTable;
            verifyFile();
            tokenizer.advance();
//            System.out.println(tokenizer.getCurrentToken());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    private void verifyFile() {
        String token = tokenizer.getCurrentToken();
        while (token != null) {
            if (TYPES.contains(token)) {
                verifyVariableDeclaration();
            } else if (token.equals(VOID)) {
                verifyFunctionDeclaration();
            } else if () {
                
            }
        }
    }

}
