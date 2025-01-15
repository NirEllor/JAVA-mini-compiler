import java.util.*;  // Importing the entire java.util package to use Random

class ChatterBot {
    static final String REQUEST_PREFIX = "say ";
    // Prefix for legal requests (e.g., "say hello")

    static final String PLACEHOLDER_FOR_REQUESTED_PHRASE = "<phrase>";
    // Placeholder for inserting the requested phrase in replies

    static final String PLACEHOLDER_FOR_ILLEGAL_REQUEST = "<request>";
    // Placeholder for inserting the illegal request in replies

    static final String ECHO = "echo";
    // Prefix for echoing back the statement

    Random rand = new Random();
    // Random object for generating random indices
    String name;  // Bot's name

    String[] legalRequestsReplies;
    // Array to store replies for legal requests
    String[] illegalRequestsReplies;
    // Array to store replies for illegal requests



    // Method to return the bot's name
    String getName() {
        return this.name;
    }

    // Method to generate a reply based on the input statement
    String replyTo(String statement) {
        if (statement.startsWith(ECHO)) {
            // Check if the statement starts with "echo"
            return statement.substring(ECHO.length());
            // Return the rest of the statement after "echo"
        }
        if (statement.startsWith(REQUEST_PREFIX)) {
            // Check if the statement is a legal request (starts with "say")
            return replyToLegalRequest(statement);
            // Handle legal request
        }
        return replyToIllegalRequest(statement);
        // Handle illegal request
    }

    // Method to handle illegal requests and return a reply with the illegal request placeholder
    String replyToIllegalRequest(String statement) {
        return replacePlaceholderInARandomPattern(this.illegalRequestsReplies,
                PLACEHOLDER_FOR_ILLEGAL_REQUEST, statement);
    }

    // Method to handle legal requests and return a reply with the requested phrase
    String replyToLegalRequest(String statement) {
        return replacePlaceholderInARandomPattern(this.legalRequestsReplies,
                PLACEHOLDER_FOR_REQUESTED_PHRASE, statement.substring(REQUEST_PREFIX.length()));
    }

    // Method to replace a placeholder in a random pattern with a given replacement
    String replacePlaceholderInARandomPattern(String[] patterns, String placeholder, String replacement) {
        int randomIndex = rand.nextInt(patterns.length);
        // Generate a random index to select a reply pattern
        String reply = patterns[randomIndex];
        // Select a random reply pattern
        return reply.replaceAll(placeholder, replacement);
        // Replace the placeholder with the given replacement and return the result
    }
}
