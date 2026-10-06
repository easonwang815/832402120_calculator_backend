package com.calculator.calculator;

/** Types of numbers, operators, functions and brackets. Unary minus uses the value -u. */
public enum TokenType {
    NUMBER,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    FUNCTION,
    LPAREN,
    RPAREN
}
