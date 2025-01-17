import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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

    private FunctionsTable functionTable;
    private SymbolTable variablesTable;
    public static final String INT = "int";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";
    public static final String DOUBLE = "double";
    public static final String STRING = "String";
    public static final String BRACKET_CLOSING = ")";
    public static final String BRACE_CLOSING = "}";
    public static final String IF = "if";
    public static final String WHILE = "while";
    public static final String RETURN = "return";
    public static final String BRACKET_OPENING = "(";
    public static final String KEYWORD = "KEYWORD";

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


    public CompilationEngine(String path, FunctionsTable functionTable) {
        try(FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            String[] listOfLines = bufferedReader.lines().toArray(String[]::new);
            this.tokenizer = new Tokenizer(listOfLines);
            this.variablesTable = new SymbolTable();
            this.functionTable = functionTable;
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
                 FinalReturnException |
                 InvalidTypeException |
                 InnerMethodDeclarationException |
                 NonExistingFunctionException |
                 IllegalReturnFormat |
                 NumberOfVarsInFuncCallException |
                 MissingVariableTypeInFunctionDeclarationException e) {
            System.out.println("1");
            System.err.println(e.getMessage());
        }

    }

    private void verifyFile() throws GlobalScopeException, InavlidVariableName, InvalidVariableDeclarationException, InvalidValueException, InvalidTypeException, FinalReturnException, InnerMethodDeclarationException, NonExistingFunctionException, IllegalReturnFormat, NumberOfVarsInFuncCallException, MissingVariableTypeInFunctionDeclarationException {

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

    private void verifyFunctionDeclaration() throws InavlidVariableName, InvalidValueException,
            InvalidVariableDeclarationException, InnerMethodDeclarationException, FinalReturnException, NonExistingFunctionException, IllegalReturnFormat, NumberOfVarsInFuncCallException, MissingVariableTypeInFunctionDeclarationException {
        boolean returnFlag = false;
        //Now token is void so move to function name
        tokenizer.advance();
        //Now token is ( so move to function name
        tokenizer.advance();
        //Now we have var dec, so move past it (verification was handled is preProcessor)
        do {
            tokenizer.advance();
        } while (!this.tokenizer.getCurrentToken().equals(BRACKET_CLOSING));
        //Now advance and handle what's in the method
        tokenizer.advance();

        String currToken = tokenizer.getCurrentToken();

        while (!currToken.equals(BRACE_CLOSING))
        {

            varOrFunctionCallCase(currToken);

            if (TYPES.contains(currToken)) {
                // Local variable declaration case
                verifyVariableDeclaration(currToken, false);
            }

            if (currToken.equals(IF)) {
                // If block case
                verifyIfBlock();
            }

            if (currToken.equals(WHILE)) {
                // While block case
                verifyWhileBlock();
            }

            if (currToken.equals(RETURN)) {
                // Return statement
                returnFlag = verifyReturnStatement();
            }

            // Method declaration inside another method error
            if (currToken.equals(VOID)) {
                // Raise inner method declaration error
                throw new InnerMethodDeclarationException();
            }

            //TODO: How to check that the closing } is in separated row? maybe is preProcessor?

            currToken = tokenizer.getCurrentToken();
        }

        // Ensure we have last return;
        if (!returnFlag) {
            //raise missing last return error
            throw new FinalReturnException();
        }

    }

    private void varOrFunctionCallCase(String currToken) throws InavlidVariableName, NonExistingFunctionException, NumberOfVarsInFuncCallException, MissingVariableTypeInFunctionDeclarationException {
        String nextToken;
        if (verifyVariableName().equals(currToken)) {
            tokenizer.lookAhead();
            nextToken = tokenizer.getCurrentToken();
            // Function call case
            if (nextToken.equals(BRACKET_OPENING)){
                if (functionTable.checkIfFunctionExist(currToken)) {
                    tokenizer.retreat();
                    verifyFunctionCall();
                } else {
                    //raise non existing function error
                    throw new NonExistingFunctionException(currToken);
                }
            } else {
                // Local variable assignment
                tokenizer.retreat();
                verifyVariableAssignment();
            }
        }
    }

    private void verifyWhileBlock() {
    }

    private void verifyIfBlock() {
    }


    private boolean verifyReturnStatement() throws IllegalReturnFormat {
        // Check legal return
        tokenizer.advance();
        if (!tokenizer.getCurrentToken().equals(EOL_COMMA)){
            // Raise illegal return format error
            throw new IllegalReturnFormat();
        }

        // Check last return
        tokenizer.lookAhead();
        if (tokenizer.getCurrentToken().equals(BRACE_CLOSING)){
            tokenizer.retreat();
            return true;
        }

        return false;

    }

    private void verifyFunctionCall() throws NumberOfVarsInFuncCallException, MissingVariableTypeInFunctionDeclarationException {
        String functionName = tokenizer.getCurrentToken();
        int varCounter = 0;
        HashMap<Integer, String> functionVarsInfo =
                functionTable.getFunctionVariables(functionName);
        // Advance to '('
        tokenizer.advance();

        // Check var validity
        tokenizer.advance();
        verifyFunctionCallVariables(functionName);
        varCounter++;

        tokenizer.advance();
        while (tokenizer.getCurrentToken().equals(COMMA)){
            if (varCounter >= functionVarsInfo.size()) {
                throw new NumberOfVarsInFuncCallException("more", functionName);
            }
            verifyFunctionCallVariables(functionName);
            varCounter++;
            tokenizer.advance();
        }

        // Check ')'
        tokenizer.advance();
//        TODO: Check with nir - This case is handled in preProcessor?
//        if (!tokenizer.getCurrentToken().equals(BRACKET_CLOSING)){
//            // Raise ')' error -> missing symbol
//        }

        // Check var list length
        if (varCounter < functionVarsInfo.size()) {
            throw new NumberOfVarsInFuncCallException("less", functionName);
        }

        // Check ;
        // TODO: Did we check in preProcessor?
        tokenizer.advance();

    }

    private void verifyFunctionCallVariables(String funcName) throws MissingVariableTypeInFunctionDeclarationException {

        // Missing type case
        if (!tokenizer.tokenType().equals(KEYWORD)){
            // missing type error
            throw new MissingVariableTypeInFunctionDeclarationException(funcName);
        }

        // check type validity and that they are assigned

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
