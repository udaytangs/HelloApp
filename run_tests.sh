#!/bin/bash

# Test runner for JSON Parser

echo "================================"
echo "JSON Parser Test Suite"
echo "================================"
echo ""

# Build first
./build.sh
if [ $? -ne 0 ]; then
    echo "Build failed, cannot run tests"
    exit 1
fi

echo ""
echo "Running tests..."
echo ""

PASSED=0
FAILED=0

# Function to run a test
run_test() {
    local file=$1
    local expected=$2
    local test_name=$(basename "$file")

    java -cp out com.jsonparser.JSONParser "$file" > /dev/null 2>&1
    local result=$?

    if [ $result -eq $expected ]; then
        echo "✓ PASS: $test_name"
        ((PASSED++))
    else
        echo "✗ FAIL: $test_name (expected exit code $expected, got $result)"
        ((FAILED++))
    fi
}

echo "Valid JSON tests (should exit with 0):"
echo "---------------------------------------"
for file in src/test/resources/valid_*.json; do
    run_test "$file" 0
done

echo ""
echo "Invalid JSON tests (should exit with 1):"
echo "---------------------------------------"
for file in src/test/resources/invalid_*.json; do
    run_test "$file" 1
done

echo ""
echo "================================"
echo "Test Results"
echo "================================"
echo "Passed: $PASSED"
echo "Failed: $FAILED"
echo "Total:  $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo "All tests passed! ✓"
    exit 0
else
    echo "Some tests failed! ✗"
    exit 1
fi
