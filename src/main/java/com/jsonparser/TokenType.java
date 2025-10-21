package com.jsonparser;

/**
 * Enum representing different types of tokens in JSON
 */
public enum TokenType {
    // Structural tokens
    LEFT_BRACE,      // {
    RIGHT_BRACE,     // }
    LEFT_BRACKET,    // [
    RIGHT_BRACKET,   // ]
    COLON,           // :
    COMMA,           // ,

    // Value tokens
    STRING,          // "string"
    NUMBER,          // 123, 45.67, -10, 1.5e10
    TRUE,            // true
    FALSE,           // false
    NULL,            // null

    // Control
    EOF              // End of file
}
