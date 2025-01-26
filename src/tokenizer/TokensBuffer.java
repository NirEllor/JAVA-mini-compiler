package tokenizer;

import java.util.List;

/**
 * This class is a new data structure to hold the tokens in the look ahead procedure
 */
public class TokensBuffer {

    private final int index;
    private final String currentToken;
    private final List<String> tokens;

    /**
     * Constructs a new {@code TokensBuffer} with the specified index, current token, and token list.
     *
     * @param index        the current index in the token list
     * @param currentToken the token currently being processed
     * @param tokens       the full list of tokens
     */
    public TokensBuffer(int index, String currentToken, List<String> tokens) {
        this.index = index;
        this.currentToken = currentToken;
        this.tokens = tokens;
    }
    /**
     * Returns the current token being processed.
     *
     * @return the current token
     */
    public String getCurrentToken() {
        return currentToken;
    }
    /**
     * Returns the current index in the token list.
     *
     * @return the current index
     */
    public int getIndex() {
        return index;
    }
    /**
     * Returns the full list of tokens.
     *
     * @return the list of tokens
     */
    public List<String> getTokens() {
        return tokens;
    }

}
