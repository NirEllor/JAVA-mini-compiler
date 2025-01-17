import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
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
    public static final int HAS_VALUE = 0;
    public static final int END_OF_LINE = 1;
    public static final int MORE_VARIABLES = 2;

    private SymbolTable variablesTable;
    public static final String INT = "int";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";
    public static final String DOUBLE = "double";
    public static final String STRING = "String";

    private static final String VALID_VARIABLE_REGEX = "^(?!_+$)(?!__)[a-zA-Z_][a-zA-Z0-9_]*$";
    public static final String VALID_INT_REGEX = "^[+-]?\\d+$";
    public static final String VALID_CHAR_REGEX = "^'[^']'$";
    public static final String VALID_DOUBLE_REGEX = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?$";
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
            } else if ( currentScope == END_OF_LINE &&
                    (variablesTable.isVariableDeclared(token) != 0) ) {
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

    private void verifyVariableDeclaration(String token, boolean isConstant) throws InavlidVariableName,
            InvalidVariableDeclarationException, InvalidValueException {
        // Whether final or not, now the token is on the type
        
        switch (token) {
            case INT:
                verifyVariable(INT, validIntPattern, isConstant);
                break;
            case CHAR:
                verifyVariable(CHAR, validCharPattern, isConstant);
                break;
            case BOOLEAN:
                verifyVariable(BOOLEAN, null, isConstant);
                break;
            case DOUBLE:
                verifyVariable(DOUBLE, validDoublePattern, isConstant);
                break;
            case STRING:
                verifyVariable(STRING, validStringPattern, isConstant);
                break;
        }
        tokenizer.advance();
    }

    private void verifyVariable(String type, Pattern valuePattern, boolean isConstant)
            throws InvalidVariableDeclarationException, InvalidValueException, InavlidVariableName {
        tokenizer.advance(); // Move to the variable name

        while (!tokenizer.getCurrentToken().equals(EOL_COMMA)) {
            // Validate and process the variable name
            String variableName = verifyVariableName();

            tokenizer.advance(); // Move to "="
            int valueStatus = verifyEqualSign(variableName, type, isConstant);

            if (valueStatus == HAS_VALUE) {
                processVariableWithValue(variableName, type, valuePattern, isConstant);
            } else if (valueStatus == END_OF_LINE) {
                break;
            } else if (valueStatus == MORE_VARIABLES) {
                tokenizer.advance(); // Move to the next variable name
            }
        }
    }

    private void processVariableWithValue(String variableName, String type, Pattern valuePattern, boolean isConstant)
            throws InvalidValueException {
        tokenizer.advance(); // Move to the value
        String variableValue = tokenizer.getCurrentToken();

        // Handle references if the value matches a variable pattern
        if (validVariablePattern.matcher(variableValue).matches()) {
            variableValue = resolveVariableReference(variableValue);
        }

        // Validate the value
        validateVariableValue(variableName, type, valuePattern, variableValue);

        // Add the variable to the symbol table
        variablesTable.declareVariable(variableName, type, variableValue, isConstant);

        tokenizer.advance(); // Move past the value
    }

    private String resolveVariableReference(String variableValue) {
        variablesTable.findVariableScope(variableValue); // Throws if the variable doesn't exist
        return variablesTable.getValue(variableValue);
    }

    private void validateVariableValue(String variableName, String type, Pattern valuePattern, String variableValue)
            throws InvalidValueException {
        if (type.equals(BOOLEAN)) {
            handleBooleanValues(variableName, variableValue);
        } else if (!valuePattern.matcher(variableValue).matches()) {
            throw new InvalidValueException(variableName, variableValue);
        }
    }


    private void handleBooleanValues(String variableName, String variableValue) throws InvalidValueException {
        if (!variableValue.equals(TRUE) && !variableValue.equals(FALSE)
                && !validDoublePattern.matcher(variableValue).matches()) {
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

    private int verifyEqualSign(String variableName, String type, boolean isConstant) throws InvalidVariableDeclarationException {
        String currentToken = tokenizer.getCurrentToken();

        // Handle assignment
        switch (currentToken) {
            case EQUALS:
                return HAS_VALUE;

            // Handle end of line or single declaration (int a;)
            case EOL_COMMA:
                variablesTable.declareVariable(variableName, type, null, isConstant);
                return END_OF_LINE;

            // Handle multiple variable declarations (int a, b;)
            case COMMA:
                variablesTable.declareVariable(variableName, type, null, isConstant);
                return MORE_VARIABLES;

            // Unexpected token
            default:
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
