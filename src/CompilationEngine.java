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
    public static final String EOL_COMMA = ";";
    public static final String COMMA = ",";
    public static final String FALSE = "false";
    public static final String TRUE = "true";

    private SymbolTable variablesTable;
    public static final String INT = "int";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";
    public static final String DOUBLE = "double";
    public static final String STRING = "String";

    private static final String VALID_VARIABLE_REGEX = "^(?!_+$)(?!__)[a-zA-Z_][a-zA-Z0-9_]*$";
    public static final String VALID_INT_REGEX = "^[+-]?\\d+$";
    public static final String VALID_CHAR_REGEX = "^'([^']|)'$";
    public static final String VALID_DOUBLE_REGEX = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?$\n";
    public static final String VALID_STRING_REGEX = "^\".*\"$";

    public Pattern validVariablePattern = Pattern.compile(VALID_VARIABLE_REGEX);
    public Pattern validIntPattern = Pattern.compile(VALID_INT_REGEX);
    public Pattern validCharPattern = Pattern.compile(VALID_CHAR_REGEX);
    public Pattern validDoublePattern = Pattern.compile(VALID_DOUBLE_REGEX);
    public Pattern validStringPattern = Pattern.compile(VALID_STRING_REGEX);


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
                 InvalidValueException |
                InvalidTypeException e) {
            System.out.println("1");
            System.err.println(e.getMessage());
        }

    }

    private void verifyFile() throws GlobalScopeException, InavlidVariableName, InvalidVariableDeclarationException, InvalidValueException, InvalidTypeException {

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

    private void verifyVariableDeclaration(String token, boolean isConstant) throws InavlidVariableName, InvalidVariableDeclarationException, InvalidValueException, InvalidTypeException {
        // Whether final or not, now the token is on the type
        
        switch (token) {
            case INT:
                verifyVariableType(INT, validIntPattern, isConstant);
                break;
            case CHAR:
                verifyVariableType(CHAR, validCharPattern, isConstant);
                break;
            case BOOLEAN:
                verifyVariableType(BOOLEAN, null, isConstant);
                break;
            case DOUBLE:
                verifyVariableType(DOUBLE, validDoublePattern, isConstant);
                break;
            case STRING:
                verifyVariableType(STRING, validStringPattern, isConstant);
                break;
        }
    }
    

    private void verifyVariableType(String type, Pattern valuePattern, boolean isConstant)
            throws InavlidVariableName, InvalidVariableDeclarationException, InvalidValueException, InvalidTypeException {
        tokenizer.advance(); // Move to variable name
        while (!tokenizer.getCurrentToken().equals(EOL_COMMA)) {
            // Validate the variable name
            String variableName = verifyVariableName();

            tokenizer.advance(); // Move to "="
            verifyEqualSign(variableName);

            tokenizer.advance(); // Move to value
            String variableValue = tokenizer.getCurrentToken();

            if (validVariablePattern.matcher(variableValue).matches()) {
                String valueType = variablesTable.getType(variableValue);
                boolean isCorrectType = compareTypes(type, valueType);
                if (!isCorrectType) {
                    throw new InvalidTypeException(variableName, type, valueType);
                }
            } else {
                // Validate the value
                if (type.equals(BOOLEAN)) {
                    verifyBoolValue(variableName, variableValue);
                } else {
                    Matcher valueMatcher = valuePattern.matcher(variableValue);
                    if (!valueMatcher.matches()) {
                        throw new InvalidValueException(variableName, variableValue);
                    }
                }
            }

            // Add the variable to the symbol table
            tokenizer.advance(); // Move past the value
            boolean endOrMoreSuccess = verifyManyVariableDeclarations(variableName);
            variablesTable.declareVariable(variableName, type, variableValue, isConstant);

            if (!endOrMoreSuccess) {
                break;
            } else {
                tokenizer.advance(); // Move to the next variable name
            }
        }
    }

    private boolean compareTypes(String type, String valueType) {
        return true;
    }

    private void verifyBoolValue(String variableName, String variableValue) throws InvalidValueException {
        if ((!variableValue.equals(TRUE)) && (!variableValue.equals(FALSE))) {
            throw new InvalidValueException(variableName, variableValue);
        }
    }


    private boolean verifyManyVariableDeclarations(String variableName) throws InvalidVariableDeclarationException {
        String currentToken = tokenizer.getCurrentToken();
        if (currentToken.equals(EOL_COMMA)) {
            return false;
        } else if (currentToken.equals(COMMA)) {
            tokenizer.advance();
            return true;
        }
        else {
            throw new InvalidVariableDeclarationException(variableName, currentToken);
        }
    }

    private void verifyEqualSign(String variableName) throws InvalidVariableDeclarationException {
        String currentToken = tokenizer.getCurrentToken();
        if (!Objects.equals(currentToken, EQUALS)) {
            throw new InvalidVariableDeclarationException(variableName, currentToken);
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
