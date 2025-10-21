package com.jsonparser;

/**
 * Exception thrown during parsing
 */
public class ParserException extends Exception {
    private final Token token;

    public ParserException(String message, Token token) {
        super(formatMessage(message, token));
        this.token = token;
    }

    private static String formatMessage(String message, Token token) {
        if (token != null) {
            return String.format("Parser error at line %d, column %d: %s",
                               token.getLine(), token.getColumn(), message);
        } else {
            return "Parser error: " + message;
        }
    }

    public Token getToken() {
        return token;
    }
}
