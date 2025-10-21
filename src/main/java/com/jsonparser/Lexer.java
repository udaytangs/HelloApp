package com.jsonparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexical analyzer for JSON input
 * Breaks input into tokens for parsing
 */
public class Lexer {
    private final String input;
    private int position = 0;
    private int line = 1;
    private int column = 1;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
    }

    /**
     * Advance to the next character in the input
     */
    private void advance() {
        if (currentChar == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }

        position++;
        if (position < input.length()) {
            currentChar = input.charAt(position);
        } else {
            currentChar = '\0'; // End of input
        }
    }

    /**
     * Peek at the next character without advancing
     */
    private char peek() {
        int peekPos = position + 1;
        if (peekPos < input.length()) {
            return input.charAt(peekPos);
        }
        return '\0';
    }

    /**
     * Skip whitespace characters
     */
    private void skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\t' ||
               currentChar == '\n' || currentChar == '\r') {
            advance();
        }
    }

    /**
     * Parse a JSON string token
     */
    private Token parseString() throws LexerException {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        advance(); // Skip opening quote

        while (currentChar != '\0' && currentChar != '"') {
            if (currentChar == '\\') {
                advance();
                if (currentChar == '\0') {
                    throw new LexerException("Unterminated string", line, column);
                }

                // Handle escape sequences
                switch (currentChar) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(currentChar);
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        // Unicode escape sequence
                        advance();
                        StringBuilder hex = new StringBuilder();
                        for (int i = 0; i < 4; i++) {
                            if (currentChar == '\0' || !isHexDigit(currentChar)) {
                                throw new LexerException("Invalid unicode escape sequence", line, column);
                            }
                            hex.append(currentChar);
                            if (i < 3) advance();
                        }
                        int codePoint = Integer.parseInt(hex.toString(), 16);
                        sb.append((char) codePoint);
                        break;
                    default:
                        throw new LexerException("Invalid escape sequence: \\" + currentChar, line, column);
                }
                advance();
            } else if (Character.isISOControl(currentChar)) {
                throw new LexerException("Unescaped control character in string", line, column);
            } else {
                sb.append(currentChar);
                advance();
            }
        }

        if (currentChar != '"') {
            throw new LexerException("Unterminated string", line, column);
        }

        advance(); // Skip closing quote
        return new Token(TokenType.STRING, sb.toString(), startLine, startColumn);
    }

    /**
     * Parse a JSON number token
     */
    private Token parseNumber() throws LexerException {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        // Optional minus sign
        if (currentChar == '-') {
            sb.append(currentChar);
            advance();
        }

        // Integer part
        if (currentChar == '0') {
            sb.append(currentChar);
            advance();
            // After 0, must be followed by . or e/E or end
            if (Character.isDigit(currentChar)) {
                throw new LexerException("Invalid number: leading zeros not allowed", line, column);
            }
        } else if (Character.isDigit(currentChar)) {
            while (Character.isDigit(currentChar)) {
                sb.append(currentChar);
                advance();
            }
        } else {
            throw new LexerException("Invalid number format", line, column);
        }

        // Fractional part
        if (currentChar == '.') {
            sb.append(currentChar);
            advance();

            if (!Character.isDigit(currentChar)) {
                throw new LexerException("Invalid number: digit expected after decimal point", line, column);
            }

            while (Character.isDigit(currentChar)) {
                sb.append(currentChar);
                advance();
            }
        }

        // Exponent part
        if (currentChar == 'e' || currentChar == 'E') {
            sb.append(currentChar);
            advance();

            if (currentChar == '+' || currentChar == '-') {
                sb.append(currentChar);
                advance();
            }

            if (!Character.isDigit(currentChar)) {
                throw new LexerException("Invalid number: digit expected in exponent", line, column);
            }

            while (Character.isDigit(currentChar)) {
                sb.append(currentChar);
                advance();
            }
        }

        return new Token(TokenType.NUMBER, sb.toString(), startLine, startColumn);
    }

    /**
     * Parse a keyword (true, false, null)
     */
    private Token parseKeyword() throws LexerException {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        while (Character.isLetter(currentChar)) {
            sb.append(currentChar);
            advance();
        }

        String keyword = sb.toString();
        TokenType type;

        switch (keyword) {
            case "true":
                type = TokenType.TRUE;
                break;
            case "false":
                type = TokenType.FALSE;
                break;
            case "null":
                type = TokenType.NULL;
                break;
            default:
                throw new LexerException("Invalid keyword: " + keyword, startLine, startColumn);
        }

        return new Token(type, keyword, startLine, startColumn);
    }

    /**
     * Check if a character is a hex digit
     */
    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    /**
     * Tokenize the entire input
     */
    public List<Token> tokenize() throws LexerException {
        List<Token> tokens = new ArrayList<>();

        while (currentChar != '\0') {
            skipWhitespace();

            if (currentChar == '\0') {
                break;
            }

            int startLine = line;
            int startColumn = column;

            switch (currentChar) {
                case '{':
                    tokens.add(new Token(TokenType.LEFT_BRACE, "{", startLine, startColumn));
                    advance();
                    break;
                case '}':
                    tokens.add(new Token(TokenType.RIGHT_BRACE, "}", startLine, startColumn));
                    advance();
                    break;
                case '[':
                    tokens.add(new Token(TokenType.LEFT_BRACKET, "[", startLine, startColumn));
                    advance();
                    break;
                case ']':
                    tokens.add(new Token(TokenType.RIGHT_BRACKET, "]", startLine, startColumn));
                    advance();
                    break;
                case ':':
                    tokens.add(new Token(TokenType.COLON, ":", startLine, startColumn));
                    advance();
                    break;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, ",", startLine, startColumn));
                    advance();
                    break;
                case '"':
                    tokens.add(parseString());
                    break;
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    tokens.add(parseNumber());
                    break;
                default:
                    if (Character.isLetter(currentChar)) {
                        tokens.add(parseKeyword());
                    } else {
                        throw new LexerException("Unexpected character: " + currentChar, line, column);
                    }
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }
}
