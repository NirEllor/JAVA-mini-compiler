import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.*;

public class PreProcessor {
    public static final String VOID = "void";
    private static final String FINAL = "final";
    private final String filePath;
    public String cleanedFilePath = "src/CleanedChatterBot.txt";
    private final FunctionsTable functionsTable;  // Instance of FunctionsTable

    String VALID_VARIABLE_REGEX = "^(?!_+$)(?!__)[a-zA-Z_][a-zA-Z0-9_]*$";
    String COMMENT_REGEX = "^(\\s*//.*|\\s*)$";
    String VALID_FUNCTION_REGEX = "^void ([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)";
    String VALID_FUNCTION_CALL_REGEX = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)";
    String END_OF_LINE_REGEX = ".*[{};]$";

    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            "int", "char", "boolean", "double", "String"));

    public Pattern commentPattern = Pattern.compile(COMMENT_REGEX);
    public Pattern validFunctionPattern = Pattern.compile(VALID_FUNCTION_REGEX);
    public Pattern validVariablePattern = Pattern.compile(VALID_VARIABLE_REGEX);
    public Pattern endOfLinePattern = Pattern.compile(END_OF_LINE_REGEX);


    public PreProcessor(String filePath, FunctionsTable functionsTable) {
        this.filePath = filePath;
        this.functionsTable = functionsTable;
    }

    // Cleans the file by removing comments and empty lines
    public void cleanFile() throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(cleanedFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (commentPattern.matcher(line).matches()) {
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
        }

        System.out.println("File cleaned successfully. Output written to " + cleanedFilePath);
    }

    // Processes the cleaned file to collect function names and validate parentheses
    public void processCleanedFile() throws IOException {
        Stack<Character> stack = new Stack<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(cleanedFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher endOfLineMatcher = endOfLinePattern.matcher(line);
                if (!endOfLineMatcher.find()) {
                    throw new EndOfLineException(line);
                }
                line = line.trim();  // Trim leading and trailing whitespace
                if (line.contains("}") && line.length() > 1) {
                    throw new ClosingRightBraceException(line);
                }

                if (line.startsWith(VOID)){
                    Matcher functionMatcher = validFunctionPattern.matcher(line);
                    if (!line.contains("(") || !line.contains(")")) {
                        throw new FunctionDeclarationException(line);
                    }
                    while (functionMatcher.find()) {
                        String functionName = functionMatcher.group(1);
//                        if (isReservedKeyword(functionName)) {
//                            throw new FunctionDeclarationException(line);
//                        }
                        String params = functionMatcher.group(2);
                        ArrayList<String> paramTypes = parseParameterTypes(params, line);
                        functionsTable.addFunction(functionName, paramTypes);  // Use FunctionsTable instance
                    }
                }

                // Validate parentheses
                for (char ch : line.toCharArray()) {
                    if (ch == '(' || ch == '{' || ch == '[') {
                        stack.push(ch);
                    } else if (ch == ')' || ch == '}' || ch == ']') {
                        if (stack.isEmpty() || !isMatchingPair(stack.pop(), ch)) {
                            throw new UnbalancedParenthesesException();

                        }
                    }
                }
            }
            System.out.println("End of lines are valid");
            System.out.println("Functions are valid");
            if (!stack.isEmpty()) {
                throw new UnbalancedParenthesesException();
            } else {
                System.out.println("All parentheses are balanced.");
            }
        } catch (EndOfLineException | UnbalancedParenthesesException |
                 ClosingRightBraceException | FunctionDeclarationException |
                 InvalidFunctionParameterException | FunctionException e) {
            System.out.println("1");
            System.err.println(e.getMessage());
            cleanedFilePath = "";
        }
    }

    private boolean isReservedKeyword(String functionName) {
        return functionName.equals("if") || functionName.equals("while");
    }

    // Parses a parameter list and returns an ArrayList of parameter types
    private ArrayList<String> parseParameterTypes(String params, String line) throws InvalidFunctionParameterException {
        ArrayList<String> paramTypes = new ArrayList<>();
        if (params.trim().isEmpty()) {
            return paramTypes;
        }
        String[] paramArray = params.split(",");
        for (String param : paramArray) {
            param = param.trim();
            String[] parts = param.split("\\s+");
            if (parts.length == 2 && validVariablePattern.matcher(parts[1]).matches() && TYPES.contains(parts[0])) {
                paramTypes.add(parts[0]);  // Add the type (first part) to the list
            } else if (parts.length == 3 && validVariablePattern.matcher(parts[2]).matches() &&
                    TYPES.contains(parts[1]) && parts[0].equals(FINAL)){
                    //TODO: how to handle final?
            } else {
                throw new InvalidFunctionParameterException(line);
            }
        }
        return paramTypes;
    }

    // Helper method to check if the parentheses match
    private boolean isMatchingPair(char open, char close) {
        return (open == '(' && close == ')') ||
                (open == '{' && close == '}') ||
                (open == '[' && close == ']');
    }

    // Runs the preprocessor (cleaning and processing)
    public String run()  {
        try {
            cleanFile();
            processCleanedFile();
        } catch (IOException e) {
            System.err.println("Couldn't open file" + " " + filePath + " ");
            cleanedFilePath = "";
        }
        return cleanedFilePath;
    }

    public static void main(String[] args) {
        String inputFile = args[0];  // Replace with the path to your input file
        FunctionsTable functionsTable = new FunctionsTable();  // Create an instance of FunctionsTable
        PreProcessor preProcessor = new PreProcessor(inputFile, functionsTable);

        String output = preProcessor.run();
//        System.out.println(output);

        // Print functions table content
        functionsTable.printFunctionsTable();
    }
}
