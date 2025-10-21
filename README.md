# JSON Parser in Java

A custom JSON parser built from scratch in Java that validates JSON input and provides meaningful error messages. This project demonstrates parsing techniques including lexical analysis (tokenization) and syntactic analysis (parsing).

## Features

- **Complete JSON Validation**: Validates JSON syntax according to the JSON specification
- **Comprehensive Type Support**:
  - Objects (empty and with key-value pairs)
  - Arrays (empty and with elements)
  - Strings (with escape sequences including Unicode)
  - Numbers (integers, floats, negative, scientific notation)
  - Booleans (true/false)
  - Null values
  - Nested structures
- **Meaningful Error Messages**: Provides line and column numbers for errors
- **Exit Codes**: Returns 0 for valid JSON, 1 for invalid JSON
- **CLI Interface**: Can parse JSON from files or command-line strings

## Architecture

The parser is implemented in three main phases:

### 1. Lexical Analysis (Lexer)
The `Lexer` class tokenizes the input string into meaningful tokens:
- Structural tokens: `{`, `}`, `[`, `]`, `:`, `,`
- Value tokens: strings, numbers, true, false, null
- Handles escape sequences in strings
- Validates number formats
- Tracks line and column positions for error reporting

### 2. Syntax Analysis (Parser)
The `Parser` class validates the token stream using recursive descent parsing:
- Implements the JSON grammar
- Validates structure and nesting
- Detects errors like trailing commas, missing quotes, etc.
- Provides meaningful error messages with locations

### 3. Main Interface (JSONParser)
The `JSONParser` class provides the CLI interface:
- Reads JSON from files or command-line arguments
- Coordinates lexer and parser
- Returns appropriate exit codes

## Project Structure

```
HelloApp/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── jsonparser/
│   │               ├── JSONParser.java      # Main entry point
│   │               ├── Lexer.java           # Tokenizer
│   │               ├── LexerException.java  # Lexer errors
│   │               ├── Parser.java          # Syntax validator
│   │               ├── ParserException.java # Parser errors
│   │               ├── Token.java           # Token representation
│   │               └── TokenType.java       # Token types enum
│   └── test/
│       └── resources/
│           ├── valid_*.json                 # Valid test cases
│           └── invalid_*.json               # Invalid test cases
├── build.sh                                 # Build script
├── run_tests.sh                            # Test runner
└── README.md                               # This file
```

## Building

To build the project, run:

```bash
./build.sh
```

This will compile all Java files and place the compiled classes in the `out/` directory.

## Usage

### Parse a JSON file:
```bash
java -cp out com.jsonparser.JSONParser <json-file>
```

### Parse a JSON string:
```bash
java -cp out com.jsonparser.JSONParser -s '{"key": "value"}'
```

### Examples:

Valid JSON:
```bash
$ java -cp out com.jsonparser.JSONParser src/test/resources/valid_simple_object.json
Valid JSON
$ echo $?
0
```

Invalid JSON:
```bash
$ java -cp out com.jsonparser.JSONParser src/test/resources/invalid_trailing_comma_object.json
Invalid JSON: Parser error at line 4, column 1: Trailing comma not allowed in object
$ echo $?
1
```

## Running Tests

The project includes 18 comprehensive test cases (9 valid, 9 invalid). Run all tests with:

```bash
./run_tests.sh
```

### Test Cases

**Valid JSON:**
- Empty objects and arrays
- Simple objects with key-value pairs
- Nested objects and arrays
- All JSON value types (string, number, boolean, null)
- Numbers with various formats (integer, float, negative, scientific notation)
- Strings with escape sequences and Unicode

**Invalid JSON:**
- Trailing commas in objects and arrays
- Missing quotes, colons, or commas
- Leading zeros in numbers
- Single quotes instead of double quotes
- Unquoted object keys
- Extra brackets or braces

## Supported JSON Grammar

```
value    := object | array | string | number | true | false | null
object   := {} | { members }
members  := pair | pair , members
pair     := string : value
array    := [] | [ elements ]
elements := value | value , elements
```

## Error Handling

The parser provides detailed error messages including:
- Error type (lexer or parser error)
- Line and column number
- Description of the error

Example error messages:
- `Lexer error at line 2, column 11: Invalid keyword: John`
- `Parser error at line 4, column 1: Trailing comma not allowed in object`
- `Lexer error at line 1, column 15: Unterminated string`

## Implementation Details

### Lexer Features:
- Character-by-character scanning
- Whitespace skipping
- String parsing with escape sequence handling
- Number validation (no leading zeros, proper decimal and exponent format)
- Keyword recognition (true, false, null)
- Position tracking for error reporting

### Parser Features:
- Recursive descent parsing
- Grammar-based validation
- Trailing comma detection
- Proper nesting validation
- Single-value JSON support (not just objects/arrays)

## Requirements

- Java 8 or higher
- Bash shell (for build and test scripts)

## Exit Codes

- `0`: Valid JSON
- `1`: Invalid JSON or error reading file

## Future Enhancements

Possible improvements:
- JSON pretty-printing
- JSON minification
- AST (Abstract Syntax Tree) generation
- JSON querying capabilities
- Performance optimizations for large files
- Streaming parser for very large JSON files

## License

This project is a coding challenge implementation for educational purposes.
