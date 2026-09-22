package com.calculator.calculator;

/**
 * 词法单元：类型 + 文本值。
 * 例如 "(1+2)*-3" 会被解析为 LPAREN("("), NUMBER("1"), PLUS("+"), NUMBER("2"),
 * RPAREN(")"), MULTIPLY("*"), MINUS("-u", 一元), NUMBER("3")。
 */
public class Token {

    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + "(" + value + ")";
    }
}
