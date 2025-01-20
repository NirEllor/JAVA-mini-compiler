import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
    public static final int GLOBAL_SCOPE = 1;
    public static final int VARIABLE_NOT_DECLARED = 0;

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
    private static final String IDENTIFIER = "IDENTIFIER";

    private static final String VALID_VARIABLE_REGEX = "^(?!_+$)(?!__)[a-zA-Z_][a-zA-Z0-9_]*$";
    public static final String VALID_INT_REGEX = "^[+-]?\\d+$";
    public static final String VALID_CHAR_REGEX = "^[^']$";
    public static final String VALID_DOUBLE_REGEX = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?$";
    public static final String VALID_STRING_REGEX = "^.*$";


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
            System.out.print("0");
        } catch (IOException e){
            System.out.print("2");
        } catch (GlobalScopeException | InavlidVariableName | InvalidVariableDeclarationException |
                 InvalidValueException | FinalReturnException | InvalidTypeException |
                 InnerMethodDeclarationException | NonExistingFunctionException | IllegalReturnFormat |
                 NumberOfVarsInFuncCallException | MissingVariableTypeInFunctionDeclarationException |
                 CallFunctionFromGlobalException | NonExistingVariableException | IllegalBlockInGlobalScope |
                 IllegalVarTypeInConditionException | UninitializedVariableInConditionException |
                 IllegalConditionException | InvalidVariableAssignmentEception | ConstantAssignmentException |
                EmptyConditionException e) {
            System.out.println("1");
            System.err.print(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void verifyFile() throws Exception, NonExistingFunctionException, NumberOfVarsInFuncCallException, IllegalBlockInGlobalScope, NonExistingVariableException, InvalidVariableAssignmentEception {

        String token = tokenizer.getCurrentToken();
        variablesTable.enterScope();
        int currentScope;

        while (token != null) {
            currentScope = variablesTable.getCurrentScope();
            if (token.equals(FINAL)) {
                tokenizer.advance();
                verifyVariableDeclaration(tokenizer.getCurrentToken(), true);
            } else if (TYPES.contains(token)) {
                tokenizer.advance();
                verifyVariableDeclaration(token, false);
            } else if (token.equals(VOID)) {
                verifyFunctionDeclaration();
            } else if ( currentScope == GLOBAL_SCOPE &&
                    (variablesTable.isVariableDeclared(token) != VARIABLE_NOT_DECLARED)) {
                verifyVariableAssignment();
            } else if (functionTable.hasFunction(token)) {
                tokenizer.lookAhead();
                if (tokenizer.getCurrentToken().equals(BRACKET_OPENING)) {
                    throw new CallFunctionFromGlobalException(token);
                }
            } else if (token.equals(WHILE) || token.equals(IF)) {
                throw new IllegalBlockInGlobalScope(token);
            } else if (tokenizer.tokenType().equals(IDENTIFIER)) {
                throw new NonExistingVariableException(token);
            } else if (token.equals(BRACE_CLOSING)) {
                tokenizer.advance();
            } else {
                    throw new GlobalScopeException();
            }
            token = tokenizer.getCurrentToken();
            variablesTable.printSymbolTable();
        }
    }



    private void verifyFunctionDeclaration() throws Exception,
            NonExistingFunctionException, NumberOfVarsInFuncCallException, InvalidVariableAssignmentEception {
        boolean returnFlag = false;
        //Now token is void so move to function name
        tokenizer.advance();
        //Now token is ( so move to function name
        tokenizer.advance();
        //Now we have var dec, so move past it (verification was handled is preProcessor)
        do {
            tokenizer.advance();
        } while (!this.tokenizer.getCurrentToken().equals(BRACKET_CLOSING));
        // Advance to {
        tokenizer.advance();
        //Now advance and handle what's in the method
        tokenizer.advance();
        String currToken = tokenizer.getCurrentToken();

        while (!currToken.equals(BRACE_CLOSING))
        {
            //System.out.println(currToken);
            if (TYPES.contains(currToken)) {
                // Local variable declaration case
                tokenizer.advance();
                verifyVariableDeclaration(currToken, false);
            } else if (currToken.equals(IF)) {
                // If block case
                verifyBlock(IF);
            } else if (currToken.equals(WHILE)) {
                // While block case
                verifyBlock(WHILE);
            } else if (currToken.equals(RETURN)) {
                // Return statement
                returnFlag = verifyReturnStatement();
            } else if (currToken.equals(VOID)) {
                // Method declaration inside another method error
                // Raise inner method declaration error
                throw new InnerMethodDeclarationException();
            } else {
                varOrFunctionCallCase(currToken);
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

    private void varOrFunctionCallCase(String currToken) throws Exception, NonExistingFunctionException, NumberOfVarsInFuncCallException, InvalidVariableAssignmentEception {
        String nextToken;
        //System.out.println("token : " + currToken);
        if (verifyVariableName(tokenizer.getCurrentToken()).equals(currToken)) {
            tokenizer.lookAhead();
            nextToken = tokenizer.getCurrentToken();
            // Function call case
            if (nextToken.equals(BRACKET_OPENING)) {
                if (functionTable.hasFunction(currToken)) {
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
                //System.out.println("got back: " + tokenizer.getCurrentToken());
            }
        }
    }

    private void verifyBlock(String blockType) throws Exception, NonExistingFunctionException, NumberOfVarsInFuncCallException, InvalidVariableAssignmentEception {
        // Advance to (
        tokenizer.advance();
        // Advance to condition
        tokenizer.advance();
        // Verify the condition + advance to )
        if (!tokenizer.getCurrentToken().equals(BRACKET_CLOSING)){
            verifyBlockCondition(blockType);
        } else {
            throw new EmptyConditionException(blockType);
        }
        // Advance to {
        tokenizer.advance();
        // Advance to after {
        tokenizer.advance();
        // Advance to verify the inner part of the block
        if (!tokenizer.getCurrentToken().equals(BRACE_CLOSING)) {
            verifyInnerPartOfBlock();
        }

        tokenizer.advance();
    }

    private void verifyInnerPartOfBlock() throws Exception, NonExistingFunctionException, NumberOfVarsInFuncCallException, InvalidVariableAssignmentEception {
        String currToken = tokenizer.getCurrentToken();

        while (!currToken.equals(BRACE_CLOSING)) {
            if (TYPES.contains(currToken)) {
                // Local variable declaration case
                tokenizer.advance();
                verifyVariableDeclaration(currToken, false);
            } else if (currToken.equals(IF)) {
                // If block case
                verifyBlock(IF);
            } else if (currToken.equals(WHILE)) {
                // While block case
                verifyBlock(WHILE);
            } else if (currToken.equals(VOID)) {
                // Method declaration inside another method error
                // Raise inner method declaration error
                throw new InnerMethodDeclarationException();
            } else {
                varOrFunctionCallCase(currToken);
                //System.out.println("now : " + currToken);
            }

            currToken = tokenizer.getCurrentToken();
        }
    }

    private void verifyBlockCondition(String blockType) throws IllegalVarTypeInConditionException, UninitializedVariableInConditionException, IllegalConditionException {
        // TODO: How to check that we dont have illegal way of using && and ||?
        do {
            verifyBlockConditionCases(blockType);
        } while (!tokenizer.getCurrentToken().equals(BRACKET_CLOSING));
    }

    private void verifyBlockConditionCases(String blockType) throws IllegalVarTypeInConditionException,
            UninitializedVariableInConditionException, IllegalConditionException {
        String token = tokenizer.getCurrentToken();
        //System.out.println("token: " + token);
        if (token.equals("||") ||
                token.equals("&&")) {
            tokenizer.advance();
        } else if (token.equals("true") ||
                token.equals("false")) {
            // Case 1 : One of the reserved words is true or false.
            tokenizer.advance();
        } else if (variablesTable.isVariableDeclared(token) > VARIABLE_NOT_DECLARED) {
            // Case 2 : An initialized boolean, double or int variable
            String varType = variablesTable.getType(token);
            if (!varType.equals(BOOLEAN) && !varType.equals(DOUBLE) && !varType.equals(INT)) {
                throw new IllegalVarTypeInConditionException(varType, blockType);
            } else if (variablesTable.getValue(token) == null) {
                // Check with nir if a variable without assignment is initialized with null
                throw new UninitializedVariableInConditionException(token, blockType);
            }
            tokenizer.advance();
        } else if ((!validDoublePattern.matcher(token).matches()) &&
                (!validIntPattern.matcher(token).matches())) {
            // Case 3 : A double or int constant/value (e.g. 5, -3, -21.5).
            throw new IllegalConditionException(blockType);
        } else {
            tokenizer.advance();
        }
    }

    private boolean verifyReturnStatement() throws IllegalReturnFormat {
        // Check legal return
        tokenizer.advance();
        if (!tokenizer.getCurrentToken().equals(EOL_COMMA)){
            // Raise illegal return format error
            throw new IllegalReturnFormat();
        }

        // Check last return
        tokenizer.advance();
        return (tokenizer.getCurrentToken().equals(BRACE_CLOSING));
    }

    private void verifyFunctionCall() throws NumberOfVarsInFuncCallException, MissingVariableTypeInFunctionDeclarationException {
        String functionName = tokenizer.getCurrentToken();
        // Move to (
        tokenizer.advance();
        // Move to first var
        verifyFunctionCallVariables(functionName);

        // Check ;
        tokenizer.advance();

    }

    //TODO : maybe ask nir how to handle.
    // Nir - check if variablesMap.getType(funcName) == functionsTable.getParameterType(funcName, parameterIndex)
    // Nir - for parameter assignment, check if variablesMap.getValue(variableName) != "null"
    private void verifyFunctionCallVariables(String functionName) throws MissingVariableTypeInFunctionDeclarationException, NumberOfVarsInFuncCallException {

        int varCounter = 0;
        HashMap<Integer, String> functionVarsInfo =
                functionTable.getFunctionVariables(functionName);

        // Check var validity
        tokenizer.advance();
        if (varCounter == functionVarsInfo.size() && !tokenizer.getCurrentToken().equals(BRACKET_CLOSING)) {
            throw new NumberOfVarsInFuncCallException("more", functionName);
        }
        if ((varCounter < functionVarsInfo.size() && tokenizer.getCurrentToken().equals(BRACKET_CLOSING))) {
            throw new NumberOfVarsInFuncCallException("less", functionName);
        }

        String currIndexType = functionVarsInfo.get(varCounter);
        verifyTypeFunctionCall(tokenizer.getCurrentToken(), currIndexType);
        varCounter++;
        tokenizer.advance();

        while (tokenizer.getCurrentToken().equals(COMMA)){
            // Move past ,
            tokenizer.advance();

            if (varCounter == functionVarsInfo.size() && !tokenizer.getCurrentToken().equals(BRACKET_CLOSING)) {
                throw new NumberOfVarsInFuncCallException("more", functionName);
            }
            if ((varCounter < functionVarsInfo.size() && tokenizer.getCurrentToken().equals(BRACKET_CLOSING))) {
                throw new NumberOfVarsInFuncCallException("less", functionName);
            }

            currIndexType = functionVarsInfo.get(varCounter);
            verifyTypeFunctionCall(tokenizer.getCurrentToken(), currIndexType);
            varCounter++;
            tokenizer.advance();
        }

        tokenizer.advance();

    }

    private void verifyTypeFunctionCall(String currentToken, String currIndexType) {

    }


    private void verifyVariableDeclaration(String token, boolean isConstant) throws Exception {
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
//        System.out.println(tokenizer.getCurrentToken());
//        tokenizer.advance();


    }

    private void verifyVariableAssignment() throws Exception, InvalidVariableAssignmentEception {
        while (!tokenizer.getCurrentToken().equals(EOL_COMMA)) {
            String variableName = tokenizer.getCurrentToken();
            variablesTable.findVariableScope(variableName); // throws if variable not declared
            String type = variablesTable.getType(variableName);
            boolean isConstant = variablesTable.isConstant(variableName);
            variableName = verifyVariableName(tokenizer.getCurrentToken());
            tokenizer.advance(); // Move to "="

            // check if variables were not assigned (e.g: a = ;, a = ,)
            if (verifyEqualSign(variableName, type, isConstant) != HAS_VALUE) {
                throw new InvalidVariableAssignmentEception(variableName);
            }
            Pattern valuePattern = getPattern(type);
            boolean isAssignment = true;
            int postAssignmentStatus = processVariableWithValue(variableName, type, valuePattern, isConstant,
                    isAssignment);
            if (postAssignmentStatus == END_OF_LINE) {
                tokenizer.advance();
                break;
            }
        }
    }

    private Pattern getPattern(String type) {
        switch (type) {
            case INT:
                return validIntPattern;
            case CHAR:
                return validCharPattern;
            case DOUBLE:
                return validDoublePattern;
            case STRING:
                return validStringPattern;
            default:
                return null;
        }
    }

    private void verifyVariable(String type, Pattern valuePattern, boolean isConstant)
            throws Exception {
        if (isConstant) {
            tokenizer.advance(); // Move to the variable name
        }

        while (!tokenizer.getCurrentToken().equals(EOL_COMMA)) {
            // Validate and process the variable name
            String variableName = verifyVariableName(tokenizer.getCurrentToken());
            tokenizer.advance(); // Move to "="
            int valueStatus = verifyEqualSign(variableName, type, isConstant);
            if (valueStatus == HAS_VALUE) {
                boolean isAssignment = false;
                int postAssignmentStatus = processVariableWithValue(variableName, type, valuePattern, isConstant, isAssignment);
                if (postAssignmentStatus == END_OF_LINE) {
                    tokenizer.advance();
                    break;
                }
            } else if (valueStatus == END_OF_LINE) {
                tokenizer.advance();
                break;
            } else if (valueStatus == MORE_VARIABLES) {
                tokenizer.advance(); // Move to the next variable name
            }
        }
    }

    private int processVariableWithValue(String variableName, String type, Pattern valuePattern, boolean isConstant, boolean isAssignment)
            throws Exception {
        tokenizer.advance(); // Move to the value

        String variableValue = tokenizer.getCurrentToken();

        // Handle references if the value matches a variable pattern
        if (variablesTable.isVariableDeclared(variableValue) != 0) {
            variableValue = resolveVariableReference(variableValue);
        }

        // Validate the value
        if (type.equals(CHAR)) {
            if (!tokenizer.getCurrentToken().equals("'"))
            {
                //raise error
                throw new Exception("char start");
            }
            tokenizer.advance();
            variableValue = tokenizer.getCurrentToken();
            if (variableValue.length() != 1){
                throw new Exception("char length");
            }
            tokenizer.advance();
            if (!tokenizer.getCurrentToken().equals("'"))
            {
                //raise error
                throw new Exception("char end");
            }
        }

        if (type.equals(STRING)) {
            if (!tokenizer.getCurrentToken().equals("\""))
            {
                //raise error
                throw new Exception("String start");
            }
            tokenizer.advance();
            variableValue = tokenizer.getCurrentToken();
            if (!variableValue.isEmpty()) {
                //TODO : do we need it?
                verifyString(variableValue);
                tokenizer.advance();
            }

            if (!tokenizer.getCurrentToken().equals("\""))
            {
                //raise error
                throw new Exception("String end");
            }

        }

        validateVariableValue(variableName, type, valuePattern, variableValue);

        // Add the variable to the symbol table
        if (isAssignment) {
            variablesTable.assignValue(variableName, variableValue);
        }
        else {
            variablesTable.declareVariable(variableName, type, variableValue, isConstant);
        }
        tokenizer.advance(); // Move past the value
        // Verify if there are more declarations or end of line
        return verifyManyVariableDeclarations(variableName, tokenizer.getCurrentToken());
    }

    private void verifyString(String variableValue) throws Exception {
        if (!validStringPattern.matcher(variableValue).matches()){
            // Raise String pattern error
            throw new Exception("String pattern");
        }
    }

    private String resolveVariableReference(String variableValue) {
//        variablesTable.findVariableScope(variableValue); // Throws if the variable doesn't exist
        return variablesTable.getValue(variableValue);
    }

    private void validateVariableValue(String variableName, String type, Pattern valuePattern, String variableValue)
            throws InvalidValueException {
        switch (type) {
            case DOUBLE:
                handleDoubleValues();
                break;
            case CHAR:
                handleCharValues();
                break;
            case STRING:
                handleStringValues();
                break;
            case BOOLEAN:
                handleBooleanValues(variableName, variableValue);
                break;
            default:
                if (!valuePattern.matcher(variableValue).matches()) {
                    throw new InvalidValueException(variableName, variableValue, type);
                }
        }
    }

    private void handleStringValues() {
    }

    private void handleCharValues() {
    }

    private void handleDoubleValues() {
    }


    private void handleBooleanValues(String variableName, String variableValue) throws InvalidValueException {
        // Booleans are either TRUE, FALSE, or valid numeric values (int or double)
        if (!variableValue.equals(TRUE) && !variableValue.equals(FALSE)
                && !validDoublePattern.matcher(variableValue).matches()) {
            throw new InvalidValueException(variableName, variableValue, BOOLEAN);
        }
        // TODO : Dont we need to add here - !validIntPattern.matcher(variableValue).matches() - ?
        // Nir - No, double also catches int
    }

    private int verifyManyVariableDeclarations(String variableName, String currentToken) throws InvalidVariableDeclarationException {

        switch (currentToken) {
            case EOL_COMMA:
                return END_OF_LINE;
            case COMMA:
                tokenizer.advance();
                return MORE_VARIABLES;
            default:
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



    private String verifyVariableName(String currentToken) throws InavlidVariableName {
        if (VALID_VARIABLE_REGEX.matches(currentToken)) {
            throw new InavlidVariableName(currentToken);
        } else {
            return currentToken;
        }
    }

}
