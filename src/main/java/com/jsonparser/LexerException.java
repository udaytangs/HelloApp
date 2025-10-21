package com.jsonparser;

/**
 * Exception thrown during lexical analysis
 */
public class LexerException extends Exception {
    private final int line;
    private final int column;

    public LexerException(String message, int line, int column) {
        super(String.format("Lexer error at line %d, column %d: %s", line, column, message));
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
