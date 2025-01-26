package tokenizer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class tokenizes the file token by token using regex. Also can return the tokens type.
 */
public class Tokenizer {

    // Constants
    private static final String SYMBOLS_REGEX = "[{}()\\[\\],;~%^+/$.\\-#@*~&|<>=\"']";
    private static final int NEXT = 0;
    private static final int LINE_INDEX = 0;
    private static final int TOKEN_LIST_INDEX = 1;
    private static final int CURRENT_TOKEN_INDEX = 2;
    private static final int EMPTY = 0;

    // Regex
    private static final String TOKEN_SPLIT = "(" + SYMBOLS_REGEX + "|\\w+)";
    private static final String IDENTIFIER_PATTERN = "^(?!_+$)(?!__)[a-zA-Z0-9_]*$";

    // Fields
    private final String[] inputCleanedLines;
    private int lineIndex;
    private List<String> tokensList;
    private String currentToken;
    private Object[] lookAheadBuffer;

    /**
     * Constructor - Creates a Tokenizer object and tokenizes the first line
     * @param inputStream : String[] - Array of lines
     */
    public Tokenizer(String[] inputStream) {
        this.inputCleanedLines = inputStream;
        this.lineIndex = 0;
        this.tokensList = new ArrayList<>();
        this.currentToken = null;
        this.lookAheadBuffer = null;
        if (inputCleanedLines.length != EMPTY) {
            this.tokensList = getTokensFromLine(inputCleanedLines[lineIndex]);
        }
    }

    /**
     * Tokenizes a single line using the regex
     * @param line : String - One single line
     * @return : List<String> - An array of the tokens
     */
    public List<String> getTokensFromLine(String line) {

        Pattern pattern = Pattern.compile(TOKEN_SPLIT);
        Matcher matcher = pattern.matcher(line);

        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        return tokens;
    }

    /**
     * Checks if there is more tokens in the file
     * @return : True if there is more tokens and False otherwise
     */
    public boolean hasMoreTokens() {
        return !tokensList.isEmpty() || lineIndex < inputCleanedLines.length - 1;
    }

    /**
     * Advances to the next token in the file
     */
    public void advance() {

        if (!hasMoreTokens()){
            currentToken = null;
        }

        while (true) {
            if (!tokensList.isEmpty()) {
                currentToken = tokensList.remove(NEXT);
            } else {
                lineIndex++;
                if (lineIndex < inputCleanedLines.length) {
                    tokensList = getTokensFromLine(inputCleanedLines[lineIndex]);
                    continue;
                } else {
                    currentToken = null;
                    break;
                }
            }
            if (!currentToken.isEmpty()) break;
        }
    }

    /**
     * Checks if the current token is of type identifier
     * @return : String - The current token's type
     */
    public boolean isIdentifier(String token) {
        return token.matches(IDENTIFIER_PATTERN);
    }

    /**
     * Getter for the currentToken field
     * @return : String - The current token
     */
    public String getCurrentToken() {
        return currentToken;
    }

    /**
     * Advances one token ahead and saves the prev
     */
    public void lookAhead() {
        lookAheadBuffer = new Object[]{lineIndex, new ArrayList<>(tokensList), currentToken};
        advance();
    }

    /**
     * Retreats to the saved token
     */
    public void retreat() {
        if (lookAheadBuffer != null) {
            lineIndex = (int) lookAheadBuffer[LINE_INDEX];
            tokensList = (List<String>) lookAheadBuffer[TOKEN_LIST_INDEX];
            currentToken = (String) lookAheadBuffer[CURRENT_TOKEN_INDEX];
            lookAheadBuffer = null;
        }
    }

}
