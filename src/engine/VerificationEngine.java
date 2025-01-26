package engine;

import tables.FunctionsTable;
import tables.SymbolTable;
import tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class verifies a given s-java file and prints 1/2 for error and 0 for success
 */
public class VerificationEngine {

    // String constants
    private static final String VOID = "void";
    private static final String FINAL = "final";
    private static final String EQUALS = "=";
    private static final String EOL_COMMA = ";";
    private static final String COMMA = ",";
    private static final String FALSE = "false";
    private static final String DOT = ".";
    private static final String BRACKET_CLOSING = ")";
    private static final String INT = "int";
    private static final String CHAR = "char";
    private static final String BOOLEAN = "boolean";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final String BRACE_CLOSING = "}";
    private static final String IF = "if";
    private static final String WHILE = "while";
    private static final String RETURN = "return";
    private static final String BRACKET_OPENING = "(";

    // Int constants
    private static final int HAS_VALUE = 0;
    private static final int END_OF_LINE = 1;
    private static final int MORE_VARIABLES = 2;
    private static final int GLOBAL_SCOPE = 1;
    private static final int VARIABLE_NOT_DECLARED = 0;
    private static final String SUCCESS = "0";
    private static final String IO_EXCEPTION = "2";
    private static final String IO_EXCEPTION_MESSAGE = "Occurred a problem while the opening/closing the file";
    private static final String EXCEPTION = "1";
    private static final String INVALID_VALUE_TYPE_EXCEPTION = "InvalidValueTypeException";
    private static final int THREE = 3;
    private static final int TWO = 2;
    private static final String MORE = "more";
    private static final String FEWER = "fewer";
    private static final String FUNCTION_CALL_VAR = "function call var";
    private static final String TRUE = "true";
    private static final String EMPTY_STRING = "";
    private static final String STRING_QUOTE = "\"";
    private static final String CHAR_QUOTE = "'";
    private static final String PLUS = "+";
    private static final String MINUS = "-";

    // Fields
    private FunctionsTable functionTable;
    private SymbolTable variablesTable;
    private Tokenizer tokenizer;

    // Regex
    private static final String VALID_INT_REGEX = "^[+-]?\\d+$";
    private static final String VALID_CHAR_REGEX = "^[^']$";
    private static final String VALID_DOUBLE_REGEX = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?$";
    private static final String VALID_STRING_REGEX = "^.*$";

    // Patterns
    private final Pattern validIntPattern = Pattern.compile(VALID_INT_REGEX);
    private final Pattern validCharPattern = Pattern.compile(VALID_CHAR_REGEX);
    private final Pattern validDoublePattern = Pattern.compile(VALID_DOUBLE_REGEX);
    private final Pattern validStringPattern = Pattern.compile(VALID_STRING_REGEX);

    // More constants
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            INT, CHAR, BOOLEAN, DOUBLE, STRING));
    private static final String OR = "|";
    private static final String AND = "&";
    private static final Set<String> AFTER_VARIABLE_VALUE_SYMBOLS  = new HashSet<>(Arrays.asList(
            EOL_COMMA, BRACKET_CLOSING, COMMA, OR, AND));
    private static final Set<String> RESERVED_NAMES =
            new HashSet<>(Arrays.asList(VOID, INT, DOUBLE, STRING, CHAR, IF, WHILE, BOOLEAN));

    /**
     * Constructor - Creates a VerificationEngine object, opens the file and verifies the
     * @param path : String  - path to the file to verify
     * @param functionTable : FunctionsTable - The object that stores all the information about
     *                     the functions in the file
     */
    public VerificationEngine(String path, FunctionsTable functionTable) {
        try(FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            String[] listOfLines = bufferedReader.lines().toArray(String[]::new);
            this.tokenizer = new Tokenizer(listOfLines);
            this.variablesTable = new SymbolTable();
            this.functionTable = functionTable;
            tokenizer.advance();
            verifyFile();
            System.out.print(SUCCESS);
        } catch (IOException e){
            System.out.print(IO_EXCEPTION);
            System.err.println(IO_EXCEPTION_MESSAGE);
        } catch (GlobalScopeException | InvalidVariableNameException | InvalidVariableDeclarationException |
                 InvalidValueTypeException | FinalReturnException |
                 InnerMethodDeclarationException | NonExistingFunctionException |
                 IllegalReturnFormatException | NumberOfVarsInFuncCallException |
                 CallFunctionFromGlobalException | NonExistingVariableException |
                 IllegalBlockInGlobalScopeException | IllegalVarTypeInConditionException |
                 UninitializedVariableInConditionException | IllegalConditionException |
                 InvalidVariableAssignmentException | ConstantAssignmentException |
                 VariableAlreadyDeclaredException | EmptyConditionException | ConstantNonAssignmentException
                 | UninitializedGlobalVariableException | IllegalInnerBlockException e) {
            System.out.println(EXCEPTION);
            System.err.print(e.getMessage());
        }

    }

    private void advanceFor(int n){
        for (int i = 0; i < n; i++) {
            tokenizer.advance();
        }
    }

    /**
     * Verifies the file
     * @throws NonExistingFunctionException - When there is a call to a non-existing function
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed
     * in function call
     * @throws IllegalBlockInGlobalScopeException - When there is an if/while block in global scope
     * @throws NonExistingVariableException - When there is access to non-existing variable
     * @throws InvalidVariableAssignmentException - When there is an illegal assignment to a variable
     * @throws VariableAlreadyDeclaredException - When there is a double declaration of variables
     * with the same name
     * @throws InvalidVariableNameException - When there is illegal variable name
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     * @throws InvalidVariableDeclarationException - When there is an error in variable declaration
     * @throws ConstantAssignmentException - When there is a try to assign a constant variable
     * @throws GlobalScopeException - When there is an illegal call in the global scope
     * @throws CallFunctionFromGlobalException - When there is a function call in the global scope
     * @throws FinalReturnException - When there last return not found in function block
     * @throws InnerMethodDeclarationException - When there was a try to declare a method inside a method
     * @throws IllegalReturnFormatException - When the return on the method is in illegal format
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     * @throws EmptyConditionException - When the condition in if/while block is empty
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     * @throws UninitializedVariableInConditionException - When the condition in if/while is of illegal type
     * @throws ConstantNonAssignmentException - When there is a constant declaration without assignment
     * @throws UninitializedGlobalVariableException - When there is a try to initialize a global variable
     * in an inner scope
     * @throws IllegalInnerBlockException - When there is illegal call in a block
     */
    private void verifyFile() throws NonExistingFunctionException, NumberOfVarsInFuncCallException,
            IllegalBlockInGlobalScopeException, NonExistingVariableException,
            InvalidVariableAssignmentException, VariableAlreadyDeclaredException,
            InvalidVariableNameException, InvalidValueTypeException,
            InvalidVariableDeclarationException, ConstantAssignmentException, GlobalScopeException,
            CallFunctionFromGlobalException, FinalReturnException, InnerMethodDeclarationException,
            IllegalReturnFormatException, IllegalConditionException, EmptyConditionException,
            IllegalVarTypeInConditionException, UninitializedVariableInConditionException,
            ConstantNonAssignmentException, UninitializedGlobalVariableException,
            IllegalInnerBlockException {

        String token = tokenizer.getCurrentToken();
        variablesTable.enterScope();
        int currentScope;

        while (token != null) {
            currentScope = variablesTable.getCurrentScope();
            if (token.equals(FINAL)) {
                tokenizer.advance();
                verifyVariableDeclaration(tokenizer.getCurrentToken(), true);
            } else if (TYPES.contains(token)) {
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
                throw new IllegalBlockInGlobalScopeException(token);
            } else if (tokenizer.isIdentifier(token)) {
                throw new NonExistingVariableException(token);
            } else if (token.equals(BRACE_CLOSING)) {
                tokenizer.advance();
            } else {
                    throw new GlobalScopeException();
            }
            token = tokenizer.getCurrentToken();
        }
    }

    /**
     * Verifies a function declaration
     * @throws NonExistingFunctionException - When there is a call to a non-existing function
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed in
     * function call
     * @throws InvalidVariableAssignmentException - When there is an illegal assignment to a variable
     * @throws VariableAlreadyDeclaredException - When there is a double declaration of variables with
     * the same name
     * @throws InvalidVariableNameException - When there is illegal variable name
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     * @throws InvalidVariableDeclarationException - When there is an error in variable declaration
     * @throws ConstantAssignmentException - When there is a try to assign a constant variable
     * @throws FinalReturnException - When there last return not found in function block
     * @throws InnerMethodDeclarationException - When there was a try to declare a method inside a method
     * @throws IllegalReturnFormatException - When the return on the method is in illegal format
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     * @throws EmptyConditionException - When the condition in if/while block is empty
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     * @throws UninitializedVariableInConditionException - When the condition in if/while is of illegal type
     * @throws ConstantNonAssignmentException - When there is a constant declaration without assignment
     * @throws UninitializedGlobalVariableException - When there is a try to initialize a global variable
     * in an inner scope
     * @throws IllegalInnerBlockException - When there is illegal call in a block
     */
    private void verifyFunctionDeclaration() throws
            NonExistingFunctionException, InvalidVariableAssignmentException,
            NumberOfVarsInFuncCallException, VariableAlreadyDeclaredException, InvalidVariableNameException,
            InvalidValueTypeException, InvalidVariableDeclarationException, ConstantAssignmentException,
            FinalReturnException, InnerMethodDeclarationException, IllegalReturnFormatException,
            IllegalConditionException, EmptyConditionException, IllegalVarTypeInConditionException,
            UninitializedVariableInConditionException, ConstantNonAssignmentException,
            UninitializedGlobalVariableException, IllegalInnerBlockException {

        variablesTable.enterScope();
        boolean returnFlag = false;

        advanceFor(THREE);

        verifyFunctionDeclarationVariables();

        advanceFor(TWO);
        String currToken = tokenizer.getCurrentToken();

        while (!currToken.equals(BRACE_CLOSING))
        {
            if (currToken.equals(RETURN)) {
                // Return statement
                returnFlag = verifyReturnStatement();
            } else {
                verifyInnerPartOfBlock();
            }

            currToken = tokenizer.getCurrentToken();
        }

        // Ensure we have last return;
        if (!returnFlag) {
            throw new FinalReturnException();
        }

        variablesTable.exitScope();
    }

    /**
     * Verifies function declaration variables
     * @throws VariableAlreadyDeclaredException - When there is a double declaration of
     * variables with the same name
     * @throws ConstantNonAssignmentException - When there is a constant declaration without assignment
     */
    private void verifyFunctionDeclarationVariables() throws ConstantNonAssignmentException,
            VariableAlreadyDeclaredException {
        boolean finalFlag = false;
        String paramType, paramName;

        while (!this.tokenizer.getCurrentToken().equals(BRACKET_CLOSING)){
            paramType = tokenizer.getCurrentToken();
            if (paramType.equals(FINAL)){
                finalFlag = true;
                tokenizer.advance();
                paramType = tokenizer.getCurrentToken();
            }
            tokenizer.advance();
            paramName = tokenizer.getCurrentToken();
            variablesTable.declareVariable(paramName, paramType, null, finalFlag, true);
            tokenizer.advance();
            if (tokenizer.getCurrentToken().equals(COMMA)){
                tokenizer.advance();
            }
        }
    }

    /**
     * Verifies the next lines inside a method/if/while block
     * @throws NonExistingFunctionException - When there is a call to a non-existing function
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed
     * in function call
     * @throws InvalidVariableAssignmentException - When there is an illegal assignment to a variable
     * @throws VariableAlreadyDeclaredException - When there is a double declaration of variables with
     * the same name
     * @throws InvalidVariableNameException - When there is illegal variable name
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     * @throws InvalidVariableDeclarationException - When there is an error in variable declaration
     * @throws ConstantAssignmentException - When there is a try to assign a constant variable
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     * @throws EmptyConditionException - When the condition in if/while block is empty
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     * @throws UninitializedVariableInConditionException - When the condition in if/while is of illegal type
     * @throws ConstantNonAssignmentException - When there is a constant declaration without assignment
     * @throws UninitializedGlobalVariableException - When there is a try to initialize a global variable
     * in an inner scope
     * @throws IllegalInnerBlockException - When there is illegal call in a block
     */
    private void verifyInnerPartOfBlock() throws ConstantNonAssignmentException,
            InvalidVariableNameException, InvalidVariableDeclarationException,
            VariableAlreadyDeclaredException, ConstantAssignmentException,
            UninitializedGlobalVariableException, InvalidValueTypeException,
            InvalidVariableAssignmentException, InnerMethodDeclarationException,
            NonExistingFunctionException, IllegalConditionException,
            EmptyConditionException, NumberOfVarsInFuncCallException, IllegalVarTypeInConditionException,
            UninitializedVariableInConditionException, IllegalInnerBlockException {

        String currToken = tokenizer.getCurrentToken();

        if (TYPES.contains(currToken)  || currToken.equals(FINAL)) {
            if (currToken.equals(FINAL)){
                tokenizer.advance();
                verifyVariableDeclaration(tokenizer.getCurrentToken(), true);
            } else {
                verifyVariableDeclaration(currToken, false);
            }
        } else if (currToken.equals(IF)) {
            variablesTable.enterScope();
            verifyBlock(IF);
            variablesTable.exitScope();
        } else if (currToken.equals(WHILE)) {
            variablesTable.enterScope();
            verifyBlock(WHILE);
            variablesTable.exitScope();
        } else if (currToken.equals(VOID)) {
            throw new InnerMethodDeclarationException();
        } else if (!varOrFunctionCallCase(currToken)) {
            throw new IllegalInnerBlockException();
        }
    }

    /**
     * Checks if it is a var or a function and verifies accordingly
     * @param currToken : String - The current token
     * @throws NonExistingFunctionException - When there is a call to a non-existing function
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed in
     * function call
     * @throws InvalidVariableAssignmentException - When there is an illegal assignment to a variable
     * @throws VariableAlreadyDeclaredException - When there is a double declaration of variables with
     * the same name
     * @throws InvalidVariableNameException - When there is illegal variable name
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     * @throws InvalidVariableDeclarationException - When there is an error in variable declaration
     * @throws ConstantAssignmentException - When there is a try to assign a constant variable
     * @throws ConstantNonAssignmentException - When there is a constant declaration without assignment
     * @throws UninitializedGlobalVariableException - When there is a try to initialize a global
     * variable in an inner scope
     */
    private boolean varOrFunctionCallCase(String currToken) throws NonExistingFunctionException,
            NumberOfVarsInFuncCallException, InvalidVariableAssignmentException,
            VariableAlreadyDeclaredException, InvalidVariableNameException, InvalidValueTypeException,
            InvalidVariableDeclarationException, ConstantAssignmentException, ConstantNonAssignmentException,
            UninitializedGlobalVariableException {

        String nextToken;

        if (verifyVariableName(tokenizer.getCurrentToken()).equals(currToken)) {
            tokenizer.lookAhead();
            nextToken = tokenizer.getCurrentToken();
            // Function call case
            if (nextToken.equals(BRACKET_OPENING)) {
                if (functionTable.hasFunction(currToken)) {
                    tokenizer.retreat();
                    verifyFunctionCall();
                    return true;
                } else {
                    throw new NonExistingFunctionException(currToken);
                }
            } else {
                // Local variable assignment
                tokenizer.retreat();
                verifyVariableAssignment();
                return true;
            }
        } else {
            return false;
        }

    }

    /**
     * Verifies an if/while block
     * @param blockType : String - if/while
     * @throws NonExistingFunctionException - When there is a call to a non-existing function
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed in
     * function call
     * @throws InvalidVariableAssignmentException - When there is an illegal assignment to a variable
     * @throws VariableAlreadyDeclaredException - When there is a double declaration of variables with the
     * same name
     * @throws InvalidVariableNameException - When there is illegal variable name
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     * @throws InvalidVariableDeclarationException - When there is an error in variable declaration
     * @throws ConstantAssignmentException - When there is a try to assign a constant variable
     * @throws InnerMethodDeclarationException - When there was a try to declare a method inside a method
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     * @throws EmptyConditionException - When the condition in if/while block is empty
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     * @throws UninitializedVariableInConditionException - When the condition in if/while is of illegal type
     * @throws ConstantNonAssignmentException - When there is a constant declaration without assignment
     * @throws UninitializedGlobalVariableException - When there is a try to initialize a global variable
     * in an inner scope
     * @throws IllegalInnerBlockException - When there is illegal call in a block
     */
    private void verifyBlock(String blockType) throws NonExistingFunctionException,
            NumberOfVarsInFuncCallException, InvalidVariableAssignmentException,
            VariableAlreadyDeclaredException, EmptyConditionException, IllegalConditionException,
            IllegalVarTypeInConditionException, UninitializedVariableInConditionException,
            InvalidVariableNameException, InvalidValueTypeException, InvalidVariableDeclarationException,
            InnerMethodDeclarationException, ConstantAssignmentException, ConstantNonAssignmentException,
            UninitializedGlobalVariableException, IllegalInnerBlockException {

        advanceFor(TWO);

        if (!tokenizer.getCurrentToken().equals(BRACKET_CLOSING)){
            verifyBlockCondition(blockType);
        } else {
            throw new EmptyConditionException(blockType);
        }

        advanceFor(TWO);

        if (!tokenizer.getCurrentToken().equals(BRACE_CLOSING)) {
            verifyInnerPartOfIfOrWhile();
        }

        tokenizer.advance();
    }

    /**
     * Verifies inner part of if/while block
     * @throws NonExistingFunctionException - When there is a call to a non-existing function
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed in
     * function call
     * @throws InvalidVariableAssignmentException - When there is an illegal assignment to a variable
     * @throws VariableAlreadyDeclaredException - When there is a double declaration of variables with the
     * same name
     * @throws InvalidVariableNameException - When there is illegal variable name
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     * @throws InvalidVariableDeclarationException - When there is an error in variable declaration
     * @throws ConstantAssignmentException - When there is a try to assign a constant variable
     * @throws InnerMethodDeclarationException - When there was a try to declare a method inside a method
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     * @throws EmptyConditionException - When the condition in if/while block is empty
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     * @throws UninitializedVariableInConditionException - When the condition in if/while is of illegal type
     * @throws ConstantNonAssignmentException - When there is a constant declaration without assignment
     * @throws UninitializedGlobalVariableException - When there is a try to initialize a global variable in
     * an inner scope
     * @throws IllegalInnerBlockException - When there is illegal call in a block
     */
    private void verifyInnerPartOfIfOrWhile() throws NonExistingFunctionException,
            NumberOfVarsInFuncCallException, InvalidVariableAssignmentException,
            VariableAlreadyDeclaredException, InvalidVariableNameException, InvalidValueTypeException,
            InvalidVariableDeclarationException, ConstantAssignmentException,
            InnerMethodDeclarationException, IllegalConditionException, EmptyConditionException,
            IllegalVarTypeInConditionException, UninitializedVariableInConditionException,
            ConstantNonAssignmentException, UninitializedGlobalVariableException,
            IllegalInnerBlockException {

        String currToken = tokenizer.getCurrentToken();

        while (!currToken.equals(BRACE_CLOSING)) {

            verifyInnerPartOfBlock();

            currToken = tokenizer.getCurrentToken();
        }
    }

    /**
     * Verifies the blocks condition
     * @param blockType : String - if/while
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     * @throws UninitializedVariableInConditionException - When the condition in if/while is of illegal type
     */
    private void verifyBlockCondition(String blockType) throws IllegalVarTypeInConditionException,
            UninitializedVariableInConditionException, IllegalConditionException {
        String currentToken = tokenizer.getCurrentToken();

        if (currentToken.equals(OR) || currentToken.equals(AND)){
            throw new IllegalConditionException(blockType);
        }

        do {
            verifyBlockConditionCases(blockType);
        } while (!tokenizer.getCurrentToken().equals(BRACKET_CLOSING));
    }

    /**
     * Verifies the block condition by cases
     * @param blockType : String - if/while
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     * @throws UninitializedVariableInConditionException - When the condition in if/while is of illegal type
     */
    private void verifyBlockConditionCases(String blockType) throws IllegalVarTypeInConditionException,
            UninitializedVariableInConditionException, IllegalConditionException {

        String token = tokenizer.getCurrentToken();
        if (token.equals(OR) || token.equals(AND)) {
            verifyAndOrCase(blockType);
        } else if (token.equals(TRUE) || token.equals(FALSE)) {
            // Case 1 : One of the reserved words is true or false.
            tokenizer.advance();
        } else if (variablesTable.isVariableDeclared(token) > VARIABLE_NOT_DECLARED) {
                verifyCaseUninitializedVariableInBlockCondition(token, blockType);
        } else if (handleDoubleValues(token).equals(INVALID_VALUE_TYPE_EXCEPTION)) {
            // Case 3 : A double or int constant/value (e.g. 5, -3, -21.5).
            throw new IllegalConditionException(blockType);
        } else {
            tokenizer.advance();
        }
    }

    /**
     * Verifies || or && case
     * @param blockType : String - if/while
     * @throws IllegalConditionException - When the condition in if/while block is illegal
     */
    private void verifyAndOrCase(String blockType) throws IllegalConditionException {
        tokenizer.advance();
        String token = tokenizer.getCurrentToken();
        if (token.equals(OR) || token.equals(AND)) {
            tokenizer.advance();
            token = tokenizer.getCurrentToken();
            if (token.equals(OR) || token.equals(AND) || token.equals(BRACKET_CLOSING)) {
                throw new IllegalConditionException(blockType);
            }
        }
    }

    /**
     * Verifies || or && case
     * @param blockType : String - if/while
     * @throws IllegalVarTypeInConditionException - When the condition in if/while is of illegal type
     *      * @throws UninitializedVariableInConditionException - When the condition in if/while is
     *      of illegal type
     */
    private void verifyCaseUninitializedVariableInBlockCondition(String token, String blockType) throws
            IllegalVarTypeInConditionException, UninitializedVariableInConditionException {
        // Case 2 : An initialized boolean, double or int variable
        String varType = variablesTable.getType(token);
        if (!varType.equals(BOOLEAN) && !varType.equals(DOUBLE) && !varType.equals(INT)) {
            throw new IllegalVarTypeInConditionException(varType, blockType);
        } else if (variablesTable.getValue(token) == null) {
            // Check with nir if a variable without assignment is initialized with null
            throw new UninitializedVariableInConditionException(token, blockType);
        }
        tokenizer.advance();
    }

    /**
     * Verifies return format and checks if it is the last one
     * @return - True if it is the last, just before '}', anf False otherwise
     * @throws IllegalReturnFormatException - When the return on the method is in illegal format

     */
    private boolean verifyReturnStatement() throws IllegalReturnFormatException {

        tokenizer.advance();
        if (!tokenizer.getCurrentToken().equals(EOL_COMMA)){
            throw new IllegalReturnFormatException();
        }

        // Check last return
        tokenizer.advance();
        return (tokenizer.getCurrentToken().equals(BRACE_CLOSING));
    }

    /**
     * Verifies function call
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed in
     * function call
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     */
    private void verifyFunctionCall() throws NumberOfVarsInFuncCallException, InvalidValueTypeException {
        String functionName = tokenizer.getCurrentToken();
        tokenizer.advance();
        verifyFunctionCallVariables(functionName);
        tokenizer.advance();

    }

    /**
     * Verifies function call variables
     * @param functionName : String - The function name
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed in
     * function call
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     */
    private void verifyFunctionCallVariables(String functionName) throws NumberOfVarsInFuncCallException,
            InvalidValueTypeException {
        int varCounter = 0;
        HashMap<Integer, String> functionVarsInfo =
                functionTable.getFunctionVariables(functionName);
        do {
            varCounter = checkVarValidityInFunctionCall(functionVarsInfo, varCounter, functionName);
        } while (tokenizer.getCurrentToken().equals(COMMA));

        tokenizer.advance();
    }

    /**
     * Verifies variables validity in function call
     * @param functionVarsInfo : HashMap<Integer, String> - Contains the information about the variable
     * from the FunctionTable
     * @param varCounter : int - Variables counter
     * @param functionName : String - The function name
     * @return int : Variables counter
     * @throws NumberOfVarsInFuncCallException - When there are more/fewer variables than needed in
     * function call
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     */
    private int checkVarValidityInFunctionCall(HashMap<Integer, String> functionVarsInfo, int varCounter,
                                               String functionName) throws
            NumberOfVarsInFuncCallException, InvalidValueTypeException {
        tokenizer.advance();

        if (varCounter == functionVarsInfo.size() && !tokenizer.getCurrentToken().equals(BRACKET_CLOSING)) {
            throw new NumberOfVarsInFuncCallException(MORE, functionName);
        }

        if ((varCounter < functionVarsInfo.size() && tokenizer.getCurrentToken().equals(BRACKET_CLOSING))) {
            throw new NumberOfVarsInFuncCallException(FEWER, functionName);
        }


        String currIndexType = functionVarsInfo.get(varCounter);

        verifyTypeFunctionCall(tokenizer.getCurrentToken(), currIndexType);

        varCounter++;
        tokenizer.advance();
        return varCounter;
    }

    /**
     * Verifies the type of the variable in a function call
     * @param currentToken : String - The current token
     * @param currIndexType : String - The type of the variable
     * @throws InvalidValueTypeException - When the value of variable is illegal, during assignment
     */
    private void verifyTypeFunctionCall(String currentToken, String currIndexType)
            throws InvalidValueTypeException {
        switch (currIndexType) {
            case INT:{
                handleIntValue(FUNCTION_CALL_VAR, tokenizer.getCurrentToken());
                break;
            }

            case DOUBLE:{
                if (handleDoubleValues(currentToken).equals(INVALID_VALUE_TYPE_EXCEPTION)){
                    throw new InvalidValueTypeException(FUNCTION_CALL_VAR, DOUBLE);
                }
                break;
            }

            case STRING: {
                handleStringValues(FUNCTION_CALL_VAR);
                break;
            }

            case BOOLEAN: {
                handleBooleanValues(FUNCTION_CALL_VAR, currentToken);
                break;
            }

            case CHAR: {
                handleCharValues(FUNCTION_CALL_VAR);
                break;
            }
        }
    }

    private String handleIntValue(String variableName, String variableValue)
            throws InvalidValueTypeException {

        if (validIntPattern.matcher(tokenizer.getCurrentToken()).matches()) {
            tokenizer.lookAhead();
            if (tokenizer.getCurrentToken().equals(DOT)){
                throw new InvalidValueTypeException(variableName, INT);
            } else {
                tokenizer.retreat();
            }
        } else {
            throw new InvalidValueTypeException(variableName, INT);
        }
        return variableValue;
    }


    private void verifyVariableDeclaration(String token, boolean isConstant)
            throws VariableAlreadyDeclaredException, InvalidVariableNameException, InvalidValueTypeException,
            InvalidVariableDeclarationException, ConstantAssignmentException, ConstantNonAssignmentException,
            UninitializedGlobalVariableException {
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


    }

    private void verifyVariableAssignment() throws InvalidVariableAssignmentException,
            VariableAlreadyDeclaredException, InvalidVariableNameException,
            InvalidVariableDeclarationException, InvalidValueTypeException, ConstantAssignmentException,
            ConstantNonAssignmentException, UninitializedGlobalVariableException {
        while (!tokenizer.getCurrentToken().equals(EOL_COMMA)) {
            String variableName = tokenizer.getCurrentToken();
            variablesTable.findVariableScope(variableName); // throws if variable not declared
            String type = variablesTable.getType(variableName);
            boolean isConstant = variablesTable.isConstant(variableName);
            variableName = verifyVariableName(tokenizer.getCurrentToken());
            tokenizer.advance(); // Move to "="

            // check if variables were not assigned (e.g: a = ;, a = ,)
            if (verifyEqualSign(variableName, type, isConstant) != HAS_VALUE) {
                throw new InvalidVariableAssignmentException(variableName);
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
        return switch (type) {
            case INT -> validIntPattern;
            case CHAR -> validCharPattern;
            case DOUBLE -> validDoublePattern;
            case STRING -> validStringPattern;
            default -> null;
        };
    }

    private void verifyVariable(String type, Pattern valuePattern, boolean isConstant)
            throws InvalidVariableNameException, InvalidVariableDeclarationException,
            InvalidValueTypeException, ConstantAssignmentException, VariableAlreadyDeclaredException,
            ConstantNonAssignmentException, UninitializedGlobalVariableException {

        while (!tokenizer.getCurrentToken().equals(EOL_COMMA)) {
            // Validate and process the variable name
            tokenizer.advance();
            String variableName = verifyVariableName(tokenizer.getCurrentToken());
            tokenizer.advance(); // Move to "="
            int valueStatus = verifyEqualSign(variableName, type, isConstant);
            if (valueStatus == HAS_VALUE) {
                boolean isAssignment = false;
                int postAssignmentStatus = processVariableWithValue(variableName, type, valuePattern,
                        isConstant, isAssignment);
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

    private int processVariableWithValue(String variableName, String type, Pattern valuePattern,
                                         boolean isConstant, boolean isAssignment)
            throws InvalidValueTypeException, ConstantAssignmentException,
            InvalidVariableDeclarationException, VariableAlreadyDeclaredException,
            ConstantNonAssignmentException, UninitializedGlobalVariableException {
        tokenizer.advance(); // Move to the value

        String variableValue = tokenizer.getCurrentToken();

        // Handle references if the value matches a variable pattern
        String sign = EMPTY_STRING;
        if (variablesTable.isVariableDeclared(variableValue) != 0) {
            variableValue = resolveVariableReference(variableValue);
        } else {
            if (type.equals(INT) || type.equals(DOUBLE) || type.equals(BOOLEAN)) {
                if (variableValue.equals(PLUS) || variableValue.equals(MINUS)) {
                    sign = variableValue;
                    tokenizer.advance();
                    variableValue = tokenizer.getCurrentToken();
                }
            }
            variableValue = validateVariableValue(variableName, type, valuePattern, variableValue);
            sign += variableValue;
            variableValue = sign;
        }

        // Add the variable to the symbol table
        if (isAssignment) {
            variablesTable.assignValue(variableName, variableValue);
        }
        else {
            variablesTable.declareVariable(variableName, type, variableValue, isConstant, false);
        }

        tokenizer.advance(); // Move past the value

        return verifyManyVariableDeclarations(variableName, tokenizer.getCurrentToken());
    }

    private String resolveVariableReference(String variableValue) {
        return variablesTable.getValue(variableValue);
    }

    private String  validateVariableValue(String variableName, String type, Pattern valuePattern,
                                          String variableValue)
            throws InvalidValueTypeException {
        switch (type) {
            case INT:
                return handleIntValue(variableName, variableValue);
            case DOUBLE:
                String result =  handleDoubleValues(variableValue);
                if (result.equals(INVALID_VALUE_TYPE_EXCEPTION)){
                    throw new InvalidValueTypeException(variableName, DOUBLE);
                }
            case CHAR:
                return handleCharValues(variableValue);
            case STRING:
                return handleStringValues(variableName);
            case BOOLEAN:
                return handleBooleanValues(variableName, variableValue);
            default:
                if (!valuePattern.matcher(variableValue).matches()) {
                    throw new InvalidValueTypeException(variableName, type);
                }
                return variableValue;
            }
    }

    private String handleStringValues(String variableName) throws InvalidValueTypeException {

        String result = EMPTY_STRING;

        if (!tokenizer.getCurrentToken().equals(STRING_QUOTE)) {
            //raise error
            throw new InvalidValueTypeException(variableName, STRING);
        }
        tokenizer.advance();
        String variableValue = tokenizer.getCurrentToken();
        if (!variableValue.equals(STRING_QUOTE)) {
            result = variableValue;
            tokenizer.advance();
        }
        if (!tokenizer.getCurrentToken().equals(STRING_QUOTE)) {
            //raise error
            throw new InvalidValueTypeException(variableName, STRING);
        }
        return result;
    }

    private String handleCharValues(String variableName) throws InvalidValueTypeException {
        if (!tokenizer.getCurrentToken().equals(CHAR_QUOTE))
        {
            //raise error
            throw new InvalidValueTypeException(variableName, CHAR);
        }
        tokenizer.advance();
        String result = tokenizer.getCurrentToken();
        if (tokenizer.getCurrentToken().length() != 1){
            throw new InvalidValueTypeException(variableName, CHAR);
        }
        tokenizer.advance();
        if (!tokenizer.getCurrentToken().equals(CHAR_QUOTE))
        {
            //raise error
            throw new InvalidValueTypeException(variableName, CHAR);
        }
        return result;
    }

    private String handleDoubleValues(String variableValue) {
        String tmp = variableValue;
        String result = tmp;
        if (validIntPattern.matcher(tmp).matches()) {
            tokenizer.lookAhead();
            tmp = tokenizer.getCurrentToken();
            if (tmp.equals(DOT)) {
                result += tmp;
                tokenizer.lookAhead();
                tmp = tokenizer.getCurrentToken();
                if (validIntPattern.matcher(tmp).matches()) {
                    result += tmp;
                } else if (AFTER_VARIABLE_VALUE_SYMBOLS.contains(tmp)) {
                    tokenizer.retreat();
                } else {
                    return INVALID_VALUE_TYPE_EXCEPTION;
                }
            } else if (!AFTER_VARIABLE_VALUE_SYMBOLS.contains(tmp)) {
                return INVALID_VALUE_TYPE_EXCEPTION;
            } else {
                tokenizer.retreat();
            }
        } else if (tmp.equals(DOT)) {
            tokenizer.lookAhead();
            tmp = tokenizer.getCurrentToken();
            if (validIntPattern.matcher(tmp).matches()) {
                result += tmp;
            } else {
                tokenizer.retreat();
                return INVALID_VALUE_TYPE_EXCEPTION;
            }
        } else {
            return INVALID_VALUE_TYPE_EXCEPTION;
        }
        return result;
    }


    private String handleBooleanValues(String variableName, String variableValue)
            throws InvalidValueTypeException {
        // Booleans are either TRUE, FALSE, or valid numeric values (int or double)
        if (!variableValue.equals(TRUE) && !variableValue.equals(FALSE)) {
            variableValue = handleDoubleValues(variableValue);
            if (variableValue.equals(INVALID_VALUE_TYPE_EXCEPTION)){
                throw new InvalidValueTypeException(variableName, BOOLEAN);
            }
        }
        return variableValue;
    }

    private int verifyManyVariableDeclarations(String variableName, String currentToken)
            throws InvalidVariableDeclarationException {
        switch (currentToken) {
            case EOL_COMMA -> {
                return END_OF_LINE;
            }
            case COMMA -> {
                return MORE_VARIABLES;
            }
            default -> throw new InvalidVariableDeclarationException(variableName, currentToken);
        }
    }

    private int verifyEqualSign(String variableName, String type, boolean isConstant)
            throws InvalidVariableDeclarationException, VariableAlreadyDeclaredException,
            ConstantNonAssignmentException {
        String currentToken = tokenizer.getCurrentToken();

        // Handle assignment
        switch (currentToken) {
            case EQUALS -> {
                return HAS_VALUE;
            }
            // Handle end of line or single declaration (int a;)
            case EOL_COMMA -> {
                variablesTable.declareVariable(variableName, type, null, isConstant, false);
                return END_OF_LINE;
            }
            // Handle multiple variable declarations (int a, b;)
            case COMMA -> {
                variablesTable.declareVariable(variableName, type, null, isConstant, false);
                return MORE_VARIABLES;
            }
            // Unexpected token
            default -> throw new InvalidVariableDeclarationException(variableName, currentToken);
        }
    }



    private String verifyVariableName(String currentToken) throws InvalidVariableNameException {
        if (!tokenizer.isIdentifier(currentToken) || RESERVED_NAMES.contains(currentToken)) {
            throw new InvalidVariableNameException(currentToken);
        } else {
            return currentToken;
        }
    }
}
