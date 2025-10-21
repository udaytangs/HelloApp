#!/bin/bash

# Build script for JSON Parser

echo "Building JSON Parser..."

# Create output directory
mkdir -p out

# Compile all Java files
javac -d out src/main/java/com/jsonparser/*.java

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "To run the parser: java -cp out com.jsonparser.JSONParser <json-file>"
else
    echo "Build failed!"
    exit 1
fi
