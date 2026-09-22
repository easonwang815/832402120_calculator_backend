package com.calculator.calculator;

/**
 * 词法单元类型。
 * NUMBER 数字；PLUS/MINUS/MULTIPLY/DIVIDE 二元运算符；LPAREN/RPAREN 括号。
 * 一元负号在求值阶段用 value = "-u" 的 MINUS Token 区分。
 */
public enum TokenType {
    NUMBER,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    LPAREN,
    RPAREN
}
