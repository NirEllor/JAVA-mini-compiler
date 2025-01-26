package preprocessor;

import tables.FunctionsTable;
import engine.InvalidCommentException;
import engine.InvalidLineFormatException;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class PreProcessor {
    private static final String VOID = "void";
    private static final String FINAL = "final";
    private static final String CLOSE_CURLY_BRACE = "}";
    private static final char END_OF_LINE = ';';
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final int TWO = 2;
    private static final char OPEN_PARENTHESIS_CHAR = '(';
    private static final char OPEN_CURLY_BRACE_CHAR = '{';
    private static final char OPEN_SQUARE_BRACKET_CHAR = '[';
    private static final char CLOSE_PARENTHESIS_CHAR = ')';
    private static final char CLOSE_CURLY_BRACE_CHAR = '}';
    private static final char CLOSE_SQUARE_BRACKETS_CHAR = ']';
    private static final String CODE_FAILURE = "1";
    private static final String IO_FAILURE = "2";
    private static final String INVALID_FILE_PATH = "";
    private static final String COMMA = ",";
    private static final String SPLIT_DELIMITER = "\\s+";
    private static final char SLASH = '/';
    private static final String COMMENT_PREFIX = "//";
    private static final String INVALID_COMMENT_OPENING = "*/";
    private static final int VARIABLE_ASSIGNED_LENGTH = 3;
    private String cleanedFilePath = "src/CleanedFile.sjava";

    private static final String INT = "int";
    private static final String CHAR = "char";
    private static final String BOOLEAN = "boolean";
    private static final String DOUBLE = "double";
    private static final String STRING = "String";
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            INT, CHAR, BOOLEAN, DOUBLE, STRING));

    private final String VALID_VARIABLE_REGEX = "^(?!_+$)(?!__)[a-zA-Z0-9_]*$";
    private final String COMMENT_REGEX = "^(\\s*//.*|\\s*)$";
    private final String VALID_FUNCTION_REGEX = "^void ([\\w]*)\\s*\\(([^)]*)\\)";
    private static final String NAME_PATTERN = "^[a-zA-Z]+[\\w]*$";
    private final String END_OF_LINE_REGEX = ".*[{};]$";

    private final Pattern commentPattern = Pattern.compile(COMMENT_REGEX);
    private final Pattern validFunctionPattern = Pattern.compile(VALID_FUNCTION_REGEX);
    private final Pattern validVariablePattern = Pattern.compile(VALID_VARIABLE_REGEX);
    private final Pattern endOfLinePattern = Pattern.compile(END_OF_LINE_REGEX);
    private final Pattern namePattern = Pattern.compile(NAME_PATTERN);

    private final String filePath;
    private final FunctionsTable functionsTable;  // Instance of FunctionsTable


    public PreProcessor(String filePath, FunctionsTable functionsTable) {
        this.filePath = filePath;
        this.functionsTable = functionsTable;
    }

    // Cleans the file by removing comments and empty lines
    private void cleanFile() throws IOException, InvalidCommentException {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(cleanedFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (commentPattern.matcher(line).matches()) {
                    continue;
                }
                if (!checkInvalidComments(line)) {
                    throw new InvalidCommentException();

                }
                writer.write(line);
                writer.newLine();
            }
        }

    }

    private boolean checkInvalidComments(String line)  {
        if ((line.length() == 1 && line.charAt(0) == SLASH) ||
                (line.length() >= TWO && line.charAt(0) == SLASH && line.charAt(1) != SLASH)) {
            return false;
        } else return !line.endsWith(INVALID_COMMENT_OPENING) || line.startsWith(COMMENT_PREFIX);
    }

    // Processes the cleaned file to collect function names and validate parentheses
    private void processCleanedFile() throws IOException {
        Stack<Character> stack = new Stack<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(cleanedFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher endOfLineMatcher = endOfLinePattern.matcher(line);
                if (!endOfLineMatcher.find()) {
                    throw new EndOfLineException(line);
                }
                line = line.trim();  // Trim leading and trailing whitespace
                if (line.contains(CLOSE_CURLY_BRACE) && line.length() > 1) {
                    throw new ClosingRightBraceException(line);
                }

                if (line.chars().filter(ch -> ch == END_OF_LINE).count() >= TWO){
                    throw new InvalidLineFormatException();
                }

                if (line.startsWith(VOID)){
                    Matcher functionMatcher = validFunctionPattern.matcher(line);
                    if (!line.contains(OPEN_PARENTHESIS) || !line.contains(CLOSE_PARENTHESIS)) {
                        throw new FunctionDeclarationException(line);
                    }
                    while (functionMatcher.find()) {
                        String functionName = functionMatcher.group(1);
                        if (!namePattern.matcher(functionName).matches()) {
                            throw new IllegalFunctionName(functionName);
                        }
                        String params = functionMatcher.group(TWO);
                        ArrayList<String> paramTypes = parseParameterTypes(params, line);
                        // Use FunctionsTable instance
                        functionsTable.addFunction(functionName, paramTypes);
                    }
                }

                // Validate parentheses
                for (char ch : line.toCharArray()) {
                    if (ch == OPEN_PARENTHESIS_CHAR || ch == OPEN_CURLY_BRACE_CHAR ||
                            ch == OPEN_SQUARE_BRACKET_CHAR) {
                        stack.push(ch);
                    } else if (ch == CLOSE_PARENTHESIS_CHAR || ch == CLOSE_CURLY_BRACE_CHAR ||
                            ch == CLOSE_SQUARE_BRACKETS_CHAR) {
                        if (stack.isEmpty() || !isMatchingPair(stack.pop(), ch)) {
                            throw new UnbalancedParenthesesException();

                        }
                    }
                }
            }
            if (!stack.isEmpty()) {
                throw new UnbalancedParenthesesException();
            }
        } catch (EndOfLineException | UnbalancedParenthesesException |
                 ClosingRightBraceException | FunctionDeclarationException |
                 InvalidFunctionParameterException | FunctionAlreadyDeclaredException |
                 InvalidLineFormatException | IllegalFunctionName e) {
            System.out.println(CODE_FAILURE);
            System.err.print(e.getMessage());
            cleanedFilePath = INVALID_FILE_PATH;
        }
    }


    // Parses a parameter list and returns an ArrayList of parameter types
    private ArrayList<String> parseParameterTypes(String params, String line) throws
            InvalidFunctionParameterException {
        ArrayList<String> paramTypes = new ArrayList<>();
        if (params.trim().isEmpty()) {
            return paramTypes;
        }
        String[] paramArray = params.split(COMMA);
        for (String param : paramArray) {
            param = param.trim();
            String[] parts = param.split(SPLIT_DELIMITER);
            if (parts.length == TWO && validVariablePattern.matcher(parts[1]).matches() &&
                    TYPES.contains(parts[0])) {
                paramTypes.add(parts[0]);  // Add the type (first part) to the list
            } else if (parts.length == VARIABLE_ASSIGNED_LENGTH &&
                    validVariablePattern.matcher(parts[TWO]).matches() &&
                    TYPES.contains(parts[1]) && parts[0].equals(FINAL)){
                paramTypes.add(parts[1]);
            } else {
                throw new InvalidFunctionParameterException(line);
            }
        }
        return paramTypes;
    }

    // Helper method to check if the parentheses match
    private boolean isMatchingPair(char open, char close) {
        return (open == OPEN_PARENTHESIS_CHAR && close == CLOSE_PARENTHESIS_CHAR) ||
                (open == OPEN_CURLY_BRACE_CHAR && close == CLOSE_CURLY_BRACE_CHAR) ||
                (open == OPEN_SQUARE_BRACKET_CHAR && close == CLOSE_SQUARE_BRACKETS_CHAR);
    }

    // Runs the preprocessor (cleaning and processing)
    public String run()  {
        try {
            cleanFile();
            processCleanedFile();
        } catch (IOException e) {
            System.out.println(IO_FAILURE);
            cleanedFilePath = INVALID_FILE_PATH;
        } catch (InvalidCommentException e) {
            System.out.println(CODE_FAILURE);
            System.err.print(e.getMessage());
            cleanedFilePath = INVALID_FILE_PATH;
        }
        return cleanedFilePath;
    }

}
