package com.jsonparser;

import java.util.List;

/**
 * Recursive descent parser for JSON
 * Validates JSON syntax according to the JSON grammar
 */
public class Parser {
    private final List<Token> tokens;
    private int position = 0;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.size() > 0 ? tokens.get(0) : null;
    }

    /**
     * Advance to the next token
     */
    private void advance() {
        position++;
        if (position < tokens.size()) {
            currentToken = tokens.get(position);
        } else {
            currentToken = null;
        }
    }

    /**
     * Check if current token matches expected type
     */
    private boolean check(TokenType type) {
        return currentToken != null && currentToken.getType() == type;
    }

    /**
     * Consume a token of the expected type, or throw error
     */
    private Token consume(TokenType type, String errorMessage) throws ParserException {
        if (!check(type)) {
            throw new ParserException(errorMessage, currentToken);
        }
        Token token = currentToken;
        advance();
        return token;
    }

    /**
     * Parse JSON input (entry point)
     * JSON = value EOF
     */
    public void parse() throws ParserException {
        if (currentToken == null) {
            throw new ParserException("Empty input", null);
        }

        parseValue();

        // After parsing the value, should only have EOF
        if (!check(TokenType.EOF)) {
            throw new ParserException("Unexpected token after JSON value: " + currentToken.getValue(), currentToken);
        }
    }

    /**
     * Parse a JSON value
     * value = object | array | string | number | true | false | null
     */
    private void parseValue() throws ParserException {
        if (check(TokenType.LEFT_BRACE)) {
            parseObject();
        } else if (check(TokenType.LEFT_BRACKET)) {
            parseArray();
        } else if (check(TokenType.STRING)) {
            advance();
        } else if (check(TokenType.NUMBER)) {
            advance();
        } else if (check(TokenType.TRUE)) {
            advance();
        } else if (check(TokenType.FALSE)) {
            advance();
        } else if (check(TokenType.NULL)) {
            advance();
        } else {
            String tokenInfo = currentToken != null ? currentToken.getValue() : "EOF";
            throw new ParserException("Expected value but got: " + tokenInfo, currentToken);
        }
    }

    /**
     * Parse a JSON object
     * object = {} | { members }
     * members = pair | pair , members
     * pair = string : value
     */
    private void parseObject() throws ParserException {
        consume(TokenType.LEFT_BRACE, "Expected '{'");

        // Empty object
        if (check(TokenType.RIGHT_BRACE)) {
            advance();
            return;
        }

        // Parse members
        parseMembers();

        consume(TokenType.RIGHT_BRACE, "Expected '}' at end of object");
    }

    /**
     * Parse object members (key-value pairs)
     */
    private void parseMembers() throws ParserException {
        // Parse first pair
        parsePair();

        // Parse additional pairs
        while (check(TokenType.COMMA)) {
            advance(); // consume comma

            // Check for trailing comma (invalid in JSON)
            if (check(TokenType.RIGHT_BRACE)) {
                throw new ParserException("Trailing comma not allowed in object", currentToken);
            }

            parsePair();
        }
    }

    /**
     * Parse a key-value pair
     */
    private void parsePair() throws ParserException {
        // Key must be a string
        consume(TokenType.STRING, "Expected string key in object");

        consume(TokenType.COLON, "Expected ':' after object key");

        // Value can be any JSON value
        parseValue();
    }

    /**
     * Parse a JSON array
     * array = [] | [ elements ]
     * elements = value | value , elements
     */
    private void parseArray() throws ParserException {
        consume(TokenType.LEFT_BRACKET, "Expected '['");

        // Empty array
        if (check(TokenType.RIGHT_BRACKET)) {
            advance();
            return;
        }

        // Parse elements
        parseElements();

        consume(TokenType.RIGHT_BRACKET, "Expected ']' at end of array");
    }

    /**
     * Parse array elements
     */
    private void parseElements() throws ParserException {
        // Parse first element
        parseValue();

        // Parse additional elements
        while (check(TokenType.COMMA)) {
            advance(); // consume comma

            // Check for trailing comma (invalid in JSON)
            if (check(TokenType.RIGHT_BRACKET)) {
                throw new ParserException("Trailing comma not allowed in array", currentToken);
            }

            parseValue();
        }
    }
}
