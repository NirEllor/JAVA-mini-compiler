import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompilationEngine {

    public static final String VOID = "void";
    public static final String FINAL = "final";
    public static final String EQUALS = "=";

    private SymbolTable variablesTable;
    public static final String INT = "int";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";
    public static final String DOUBLE = "double";
    public static final String STRING = "String";

    public String VALID_VARIABLE_REGEX = "^(?!_+$)(?!__)[a-zA-Z_][a-zA-Z0-9_]*$";
    public static final String VALID_INT_REGEX = "^-?\\d+$";

    public Pattern validVariablePattern = Pattern.compile(VALID_VARIABLE_REGEX);
    public Pattern validIntPattern = Pattern.compile(VALID_INT_REGEX);

    private Tokenizer tokenizer;

    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            INT, CHAR, BOOLEAN, DOUBLE, STRING));

    public CompilationEngine(String path, FunctionsTable functionsTable) {
        try(FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            String[] listOfLines = bufferedReader.lines().toArray(String[]::new);
            this.tokenizer = new Tokenizer(listOfLines);
            this.variablesTable = new SymbolTable();
            tokenizer.advance();
            verifyFile();
            System.out.println("0");
        } catch (IOException e){
            System.out.println("2");
            System.out.println(e.getMessage());
        } catch (GlobalScopeException |
                 InavlidVariableName |
                 InvalidVariableDeclarationException |
                 InvalidIntValueException e) {
            System.out.println("1");
            System.err.println(e.getMessage());
        }

    }

    private void verifyFile() throws GlobalScopeException, InavlidVariableName, InvalidVariableDeclarationException, InvalidIntValueException {

        String token = tokenizer.getCurrentToken();
        variablesTable.enterScope();
        int currentScope;

        while (token != null) {

            System.out.println(token);
            currentScope = variablesTable.getCurrentScope();

            if (TYPES.contains(token)) {
                verifyVariableDeclaration(token, false);
            } else if (token.equals(FINAL)) {
                tokenizer.advance();
                verifyVariableDeclaration(token, true);
            } else if (token.equals(VOID)) {
                verifyFunctionDeclaration();
            } else if ( currentScope == 1 &&
                    variablesTable.getVariablesMap().get(currentScope).containsKey(token) ) {
                verifyVariableAssignment();
            } else {
                throw new GlobalScopeException();
            }

            token = tokenizer.getCurrentToken();
        }
    }


    private void verifyVariableAssignment() {
        tokenizer.advance();

    }

    private void verifyFunctionDeclaration() {
        tokenizer.advance();

    }

    private void verifyVariableDeclaration(String token, boolean isConstant) throws InavlidVariableName, InvalidVariableDeclarationException, InvalidIntValueException {
        // Whether final or not, now the token is on the type
        
        switch (token) {
            case INT:
                verifyInt(isConstant);
                break;
            case CHAR:
                verifyChar(isConstant);
                break;
            case BOOLEAN:
                verifyBoolean(isConstant);
                break;
            case DOUBLE:
                verifyDouble(isConstant);
                break;
            case STRING:
                verifyString(isConstant);
                break;
        }
    }

    private void verifyString(boolean isConstant) {

    }

    private void verifyDouble(boolean isConstant) {
    }

    private void verifyBoolean(boolean isConstant) {
    }

    private void verifyChar(boolean isConstant) {
    }

    private void verifyInt(boolean isConstant) throws InavlidVariableName,
            InvalidVariableDeclarationException, InvalidIntValueException {
        tokenizer.advance(); // move to name
        String variableName = verifyVariableName();
        tokenizer.advance(); // move to "="
        verifyEqualSign(variableName);
        tokenizer.advance(); // move to value

        String variableValue = tokenizer.getCurrentToken();
        Matcher intMatcher = validIntPattern.matcher(variableValue);
        if (!intMatcher.matches()) {
            throw new InvalidIntValueException(variableName, variableValue);
        }


    }

    private void verifyEqualSign(String variableName) throws InvalidVariableDeclarationException {
        String currentToken = tokenizer.getCurrentToken();
        if (!Objects.equals(currentToken, EQUALS)) {
            throw new InvalidVariableDeclarationException(variableName);
        }
    }



    private String verifyVariableName() throws InavlidVariableName {
        String currentToken = tokenizer.getCurrentToken();
        Matcher variableMatcher = validVariablePattern.matcher(currentToken);
        if (!variableMatcher.find()) {
            throw new InavlidVariableName(currentToken);
        } else {
            return currentToken;
        }
    }

}
