
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private static final String SYMBOLS_REGEX = "[{}()\\[\\],;~%^+/$.\\-#@*~&|<>=\"']";
    private static final Pattern SYMBOLS_PATTERN = Pattern.compile(SYMBOLS_REGEX);
    private static final String INT_REGEX = "^(?:[1-9][0-9]{0,4}|0|32767)$";
    private static final Pattern INT_PATTERN = Pattern.compile(INT_REGEX);

    // Define the regex pattern for splitting tokens
    private static final String TOKEN_SPLIT = "(" + SYMBOLS_REGEX + "|\\w+|(-)?\\d+)";
    private static final String IDENTIFIER_PATTERN = "^(?!_+$)(?!__)[a-zA-Z_][a-zA-Z0-9_]*$";


    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList("int", "char",
            "boolean", "void", "true", "false", "null", "if", "else", "while", "return", "double", "String"));

    private final String[] inputCleanedLines;
    private int lineIndex;
    private List<String> tokensList;
    private String currentToken;
    private Object[] lookAheadBuffer;

    public Tokenizer(String[] inputStream) {
        this.inputCleanedLines = inputStream;
        this.lineIndex = 0;
        this.tokensList = new ArrayList<>();
        this.currentToken = null;
        this.lookAheadBuffer = null;
        if (inputCleanedLines.length != 0) {
            this.tokensList = getTokensFromLine(inputCleanedLines[lineIndex]);
        }
    }


    public List<String> getTokensFromLine(String line) {

        Pattern pattern = Pattern.compile(TOKEN_SPLIT);
        Matcher matcher = pattern.matcher(line);

        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        return tokens;
    }


    public boolean hasMoreTokens() {
        return !tokensList.isEmpty() || lineIndex < inputCleanedLines.length - 1;
    }

    public void advance() {

        if (!hasMoreTokens()){
            currentToken = null;
        }

        while (true) {
            if (!tokensList.isEmpty()) {
                currentToken = tokensList.remove(0);
//                System.out.println("current token: " + currentToken);
            } else {
                lineIndex++;
                if (lineIndex < inputCleanedLines.length) {
                    tokensList = getTokensFromLine(inputCleanedLines[lineIndex]);
                    System.out.println(tokensList.toString());
                    continue;
                } else {
                    currentToken = null;
                    break;
                }
            }
            if (!currentToken.isEmpty()) break;
        }
    }

    public String tokenType() {
        if (currentToken == null) return "No current token";
        if (checkKeyword()) return "KEYWORD";
        if (checkIdentifier()) return "IDENTIFIER";
        if (checkSymbol()) return "SYMBOL";
        if (checkIntVal()) return "NUMBER";
        return null;
    }

    public String getCurrentToken() {
        return currentToken;
    }

//    public String keyword() {
//        return checkKeyword() ? currentToken : null;
//    }
//
//    public String symbol() {
//        return checkSymbol() ? currentToken : null;
//    }
//
//    public String identifier() {
//        return checkIdentifier() ? currentToken : null;
//    }

    public boolean checkIntVal() {
        return INT_PATTERN.matcher(currentToken).matches();
    }

    private boolean checkSymbol() {
        return SYMBOLS_PATTERN.matcher(currentToken).matches();
    }

    private boolean checkKeyword() {
        return KEYWORDS.contains(currentToken);
    }

    private boolean checkIdentifier() {
        return currentToken.matches(IDENTIFIER_PATTERN);
    }

    public void lookAhead() {
        lookAheadBuffer = new Object[]{lineIndex, new ArrayList<>(tokensList), currentToken};
        advance();
    }

    public void retreat() {
        if (lookAheadBuffer != null) {
            lineIndex = (int) lookAheadBuffer[0];
            tokensList = (List<String>) lookAheadBuffer[1];
            currentToken = (String) lookAheadBuffer[2];
            lookAheadBuffer = null;
        }
    }

    public String[] getCurrentTokenArray() {
        return tokensList.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String[] input = new String[]{"int -_a1 = -1.;", "\'yuli\'"};
        Tokenizer tokenizer = new Tokenizer(input);

        tokenizer.advance();
        while (tokenizer.currentToken != null) {
            System.out.println(tokenizer.currentToken + " " + tokenizer.tokenType());
            tokenizer.advance();
        }
    }


}
