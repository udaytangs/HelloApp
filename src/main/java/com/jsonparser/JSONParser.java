package com.jsonparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class for the JSON Parser
 * Provides CLI interface for validating JSON files
 */
public class JSONParser {

    /**
     * Validate JSON from a string
     */
    public static boolean validateJSON(String json) {
        try {
            // Lexical analysis
            Lexer lexer = new Lexer(json);
            List<Token> tokens = lexer.tokenize();

            // Syntax analysis
            Parser parser = new Parser(tokens);
            parser.parse();

            return true;
        } catch (LexerException | ParserException e) {
            System.err.println("Invalid JSON: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate JSON from a file
     */
    public static boolean validateFile(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            return validateJSON(content);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java JSONParser <json-file>");
            System.err.println("       java JSONParser -s \"<json-string>\"");
            System.exit(1);
        }

        boolean isValid;

        if (args[0].equals("-s") || args[0].equals("--string")) {
            // Parse JSON string from command line
            if (args.length < 2) {
                System.err.println("Error: No JSON string provided");
                System.exit(1);
            }
            isValid = validateJSON(args[1]);
        } else {
            // Parse JSON from file
            isValid = validateFile(args[0]);
        }

        if (isValid) {
            System.out.println("Valid JSON");
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
