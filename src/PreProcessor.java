import java.io.*;
import java.util.*;
import java.util.regex.*;

public class PreProcessor {
    private final String filePath;
    public String cleanedFilePath = "src/CleanedChatterBot.txt";
    private final FunctionsTable functionsTable;  // Instance of FunctionsTable
    public boolean success = false;

    public PreProcessor(String filePath, FunctionsTable functionsTable) {
        this.filePath = filePath;
        this.functionsTable = functionsTable;
    }

    // Cleans the file by removing comments and empty lines
    public void cleanFile() throws IOException {
        Pattern commentPattern = Pattern.compile("^(\\s*//.*|\\s*)$");

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
        Pattern functionPattern = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)");
        Stack<Character> stack = new Stack<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(cleanedFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Collect function names and parameter types
                Matcher matcher = functionPattern.matcher(line);
                while (matcher.find()) {
                    String functionName = matcher.group(1);
                    String params = matcher.group(2);
                    ArrayList<String> paramTypes = parseParameterTypes(params);
                    functionsTable.addFunction(functionName, paramTypes);  // Use FunctionsTable instance
                }

                // Validate parentheses
                for (char ch : line.toCharArray()) {
                    if (ch == '(' || ch == '{' || ch == '[') {
                        stack.push(ch);
                    } else if (ch == ')' || ch == '}' || ch == ']') {
                        if (stack.isEmpty() || !isMatchingPair(stack.pop(), ch)) {
                            System.out.println("Unbalanced parentheses found.");
                            success = false;
                            return;
                        }
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            System.out.println("Unbalanced parentheses found.");
            success = false;
        } else {
            System.out.println("All parentheses are balanced.");
            success = true;
        }
    }

    // Parses a parameter list and returns an ArrayList of parameter types
    private ArrayList<String> parseParameterTypes(String params) {
        ArrayList<String> paramTypes = new ArrayList<>();
        if (params.trim().isEmpty()) {
            return paramTypes;
        }

        String[] paramArray = params.split(",");
        for (String param : paramArray) {
            param = param.trim();
            String[] parts = param.split("\\s+");
            if (parts.length > 1) {
                paramTypes.add(parts[0]);  // Add the type (first part) to the list
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
            e.printStackTrace();
            cleanedFilePath = "preprocessor failed";
        }
        return cleanedFilePath;
    }

    public static void main(String[] args) {
        String inputFile = "src/ChatterBot.java";  // Replace with the path to your input file
        FunctionsTable functionsTable = new FunctionsTable();  // Create an instance of FunctionsTable
        PreProcessor preProcessor = new PreProcessor(inputFile, functionsTable);

        String output = preProcessor.run();
        System.out.println(output);

        // Print functions table content
        functionsTable.printFunctionsTable();
    }
}
