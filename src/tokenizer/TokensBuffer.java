package tokenizer;

import java.util.List;

/**
 * This class is a new data structure to hold the tokens in the look ahead procedure
 */
public class TokensBuffer {

    private final int index;
    private final String currentToken;
    private final List<String> tokens;

    public TokensBuffer(int index, String currentToken, List<String> tokens) {
        this.index = index;
        this.currentToken = currentToken;
        this.tokens = tokens;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public int getIndex() {
        return index;
    }

    public List<String> getTokens() {
        return tokens;
    }

}
