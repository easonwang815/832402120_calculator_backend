package com.calculator.calculator;

import java.util.ArrayList;
import java.util.List;

/** Splits an expression into numbers, operators, functions and brackets. Spaces are ignored. */
public class ExpressionParser {

    /** Returns tokens for the input expression, or reports invalid input. */
    public List<Token> parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new CalculatorException("Invalid expression");
        }

        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int n = input.length();

        while (i < n) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                int start = i;
                boolean hasDot = false;
                boolean hasDigit = false;
                while (i < n && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    if (input.charAt(i) == '.') {
                        if (hasDot) {
                            throw new CalculatorException("Invalid expression"); // 1.2.3
                        }
                        hasDot = true;
                    } else {
                        hasDigit = true;
                    }
                    i++;
                }
                String num = input.substring(start, i);
                // A dot alone is invalid, but .5 and 1. are valid decimal forms.
                if (!hasDigit) {
                    throw new CalculatorException("Invalid expression");
                }
                tokens.add(new Token(TokenType.NUMBER, num));
            } else if (Character.isLetter(c)) {
                int start = i;
                while (i < n && Character.isLetter(input.charAt(i))) {
                    i++;
                }
                String function = input.substring(start, i).toLowerCase();
                if (!isSupportedFunction(function)) {
                    throw new CalculatorException("Invalid expression");
                }
                tokens.add(new Token(TokenType.FUNCTION, function));
            } else {
                switch (c) {
                    case '+':
                        tokens.add(new Token(TokenType.PLUS, "+"));
                        i++;
                        break;
                    case '-':
                        tokens.add(new Token(TokenType.MINUS, "-"));
                        i++;
                        break;
                    case '*':
                        tokens.add(new Token(TokenType.MULTIPLY, "*"));
                        i++;
                        break;
                    case '/':
                        tokens.add(new Token(TokenType.DIVIDE, "/"));
                        i++;
                        break;
                    case '^':
                        tokens.add(new Token(TokenType.POWER, "^"));
                        i++;
                        break;
                    case '(':
                        tokens.add(new Token(TokenType.LPAREN, "("));
                        i++;
                        break;
                    case ')':
                        tokens.add(new Token(TokenType.RPAREN, ")"));
                        i++;
                        break;
                    default:
                        throw new CalculatorException("Invalid expression");
                }
            }
        }

        if (tokens.isEmpty()) {
            throw new CalculatorException("Invalid expression");
        }
        return tokens;
    }

    private boolean isSupportedFunction(String function) {
        return "sin".equals(function)
                || "cos".equals(function)
                || "tan".equals(function)
                || "sqrt".equals(function);
    }
}
