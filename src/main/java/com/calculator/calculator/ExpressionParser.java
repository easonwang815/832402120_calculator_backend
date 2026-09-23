package com.calculator.calculator;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器：把表达式字符串拆成 Token 列表。
 *
 * 支持的语法：
 * - 数字（含小数，如 12、3.14）
 * - 二元运算符 + - * / ^
 * - 科学函数 sin / cos / tan / sqrt
 * - 括号 ( )
 * - 一元正负号（如 -5、3*-2、+5），一元号在后续调度场算法中处理
 *
 * 输入中的空白字符会被忽略；遇到不支持的字符直接抛 Invalid expression。
 */
public class ExpressionParser {

    /**
     * 将表达式字符串解析为 Token 列表。
     *
     * @param input 表达式原文，如 "(1+2)*-3"
     * @return Token 列表
     * @throws CalculatorException 表达式为空或包含非法字符时抛出
     */
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
                // 单独一个 "." 不构成数字；允许 .5 和 1. 这类常见小数写法。
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
