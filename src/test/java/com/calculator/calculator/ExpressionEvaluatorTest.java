package com.calculator.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 表达式求值器单元测试：覆盖作业要求的全部核心场景。
 */
class ExpressionEvaluatorTest {

    private ExpressionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ExpressionEvaluator();
    }

    @Test
    @DisplayName("基础四则运算")
    void basicOperations() {
        assertEquals("20", evaluator.evaluate("12+8"));
        assertEquals("4", evaluator.evaluate("10-6"));
        assertEquals("40", evaluator.evaluate("5*8"));
        assertEquals("4", evaluator.evaluate("20/5"));
    }

    @Test
    @DisplayName("运算符优先级")
    void precedence() {
        assertEquals("7", evaluator.evaluate("1+2*3"));
        assertEquals("12", evaluator.evaluate("10/2+7"));
        assertEquals("2", evaluator.evaluate("8-3*2"));
    }

    @Test
    @DisplayName("括号")
    void parentheses() {
        assertEquals("9", evaluator.evaluate("(1+2)*3"));
        assertEquals("20", evaluator.evaluate("(2+3)*4"));
    }

    @Test
    @DisplayName("一元正负号")
    void unaryOperators() {
        assertEquals("3", evaluator.evaluate("-5+8"));
        assertEquals("-6", evaluator.evaluate("3*-2"));
        assertEquals("5", evaluator.evaluate("+5"));
        assertEquals("-5", evaluator.evaluate("-5"));
        assertEquals("5", evaluator.evaluate("--5"));
        assertEquals("-9", evaluator.evaluate("-(1+2)*3"));
        // 一元正负号可跟在运算符后（数学上合法：1+(+2)、1+(-2)）
        assertEquals("3", evaluator.evaluate("1++2"));
        assertEquals("-1", evaluator.evaluate("1+-2"));
    }

    @Test
    @DisplayName("小数计算")
    void decimals() {
        assertEquals("10", evaluator.evaluate("2.5*4"));
        assertEquals("6.3", evaluator.evaluate("1.2+5.1"));
        assertEquals("0.5", evaluator.evaluate("1/2"));
        assertEquals("0.5", evaluator.evaluate(".5"));
        assertEquals("1", evaluator.evaluate("1."));
        assertEquals("3.1428571429", evaluator.evaluate("22/7"));
    }

    @Test
    @DisplayName("除零处理")
    void divisionByZero() {
        CalculatorException ex = assertThrows(CalculatorException.class, () -> evaluator.evaluate("10/0"));
        assertEquals("Division by zero", ex.getMessage());
    }

    @Test
    @DisplayName("无效表达式处理")
    void invalidExpressions() {
        assertInvalid("");
        assertInvalid("   ");
        assertInvalid("abc");
        assertInvalid("1+");
        assertInvalid("+");
        assertInvalid("(1+2");
        assertInvalid("1+2)");
        assertInvalid("1.2.3");
        assertInvalid(".");
        assertInvalid("()");
        assertInvalid("*3");
        assertInvalid("**2");
        assertInvalid("1 2");
        assertInvalid("1/");
    }

    private void assertInvalid(String expr) {
        CalculatorException ex = assertThrows(CalculatorException.class, () -> evaluator.evaluate(expr));
        assertEquals("Invalid expression", ex.getMessage());
    }
}
