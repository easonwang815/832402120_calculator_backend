package com.calculator.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * 表达式求值器：采用调度场算法（Shunting-yard）。
 *
 * 流程：
 * 1. 中缀表达式 → 后缀表达式（逆波兰式），天然解决运算符优先级与括号；
 * 2. 后缀表达式用数值栈求值；
 * 3. 数字统一使用 BigDecimal 计算，除法保留 10 位小数（HALF_UP），避免浮点精度问题。
 *
 * 一元正负号处理：当 '-' 出现在表达式开头，或前一个 Token 是运算符/左括号时，
 * 视为一元负号（Token value = "-u"，优先级最高），求值时对栈顶数字取负。
 * 一元正号（如 +5）直接忽略，不影响数值。
 *
 * 不使用 eval / ScriptEngine 等执行任意代码的方式，只做数学运算，满足作业安全要求。
 */
public class ExpressionEvaluator {

    /** 除法结果保留的小数位数 */
    private static final int SCALE = 10;

    /** 一元负号标记 */
    private static final String UNARY_MINUS = "-u";

    /**
     * 计算表达式并返回格式化后的结果字符串。
     *
     * @param expression 表达式原文，如 "(1+2)*3"
     * @return 计算结果，如 "9"、"0.5"
     * @throws CalculatorException 无效表达式或除零
     */
    public String evaluate(String expression) {
        List<Token> tokens = new ExpressionParser().parse(expression);
        List<Token> postfix = toPostfix(tokens);
        BigDecimal result = evaluatePostfix(postfix);
        // stripTrailingZeros 去掉尾零（5.0000000000 → 5），toPlainString 避免科学计数法
        return result.stripTrailingZeros().toPlainString();
    }

    /**
     * 调度场算法：中缀 Token 列表 → 后缀（逆波兰）Token 列表。
     */
    private List<Token> toPostfix(List<Token> tokens) {
        Deque<Token> opStack = new ArrayDeque<>();
        List<Token> output = new java.util.ArrayList<>();
        boolean expectOperand = true; // 期望操作数：用于判断 '-' 是否为一元

        for (Token token : tokens) {
            switch (token.getType()) {
                case NUMBER:
                    output.add(token);
                    expectOperand = false;
                    break;

                case PLUS:
                    if (expectOperand) {
                        // 一元正号：直接忽略，不产生任何 Token
                        break;
                    }
                    while (!opStack.isEmpty() && precedence(opStack.peek()) >= 1) {
                        output.add(opStack.pop());
                    }
                    opStack.push(token);
                    expectOperand = true;
                    break;

                case MINUS:
                    if (expectOperand) {
                        // 一元负号：压入高优先级标记
                        opStack.push(new Token(TokenType.MINUS, UNARY_MINUS));
                        break;
                    }
                    while (!opStack.isEmpty() && precedence(opStack.peek()) >= 1) {
                        output.add(opStack.pop());
                    }
                    opStack.push(token);
                    expectOperand = true;
                    break;

                case MULTIPLY:
                case DIVIDE:
                    while (!opStack.isEmpty() && precedence(opStack.peek()) >= 2) {
                        output.add(opStack.pop());
                    }
                    opStack.push(token);
                    expectOperand = true;
                    break;

                case LPAREN:
                    opStack.push(token);
                    expectOperand = true;
                    break;

                case RPAREN:
                    // 弹出直到左括号；若找不到左括号则括号不匹配
                    boolean matched = false;
                    while (!opStack.isEmpty()) {
                        Token top = opStack.pop();
                        if (top.getType() == TokenType.LPAREN) {
                            matched = true;
                            break;
                        }
                        output.add(top);
                    }
                    if (!matched) {
                        throw new CalculatorException("Invalid expression");
                    }
                    expectOperand = false;
                    break;

                default:
                    throw new CalculatorException("Invalid expression");
            }
        }

        // 弹出剩余运算符；若栈中残留括号说明括号不匹配
        while (!opStack.isEmpty()) {
            Token top = opStack.pop();
            if (top.getType() == TokenType.LPAREN || top.getType() == TokenType.RPAREN) {
                throw new CalculatorException("Invalid expression");
            }
            output.add(top);
        }

        return output;
    }

    /**
     * 运算符优先级：+ - 为 1，* / 为 2，一元负号 3，括号 0。
     */
    private int precedence(Token token) {
        switch (token.getType()) {
            case PLUS:
            case MINUS:
                if (UNARY_MINUS.equals(token.getValue())) {
                    return 3;
                }
                return 1;
            case MULTIPLY:
            case DIVIDE:
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 后缀表达式求值：数值栈。
     */
    private BigDecimal evaluatePostfix(List<Token> postfix) {
        Deque<BigDecimal> stack = new ArrayDeque<>();

        for (Token token : postfix) {
            switch (token.getType()) {
                case NUMBER:
                    stack.push(new BigDecimal(token.getValue()));
                    break;

                case PLUS:
                    stack.push(applyBinary(stack, (a, b) -> a.add(b)));
                    break;

                case MINUS:
                    if (UNARY_MINUS.equals(token.getValue())) {
                        if (stack.isEmpty()) {
                            throw new CalculatorException("Invalid expression");
                        }
                        stack.push(stack.pop().negate());
                    } else {
                        stack.push(applyBinary(stack, (a, b) -> a.subtract(b)));
                    }
                    break;

                case MULTIPLY:
                    stack.push(applyBinary(stack, (a, b) -> a.multiply(b)));
                    break;

                case DIVIDE:
                    if (stack.size() < 2) {
                        throw new CalculatorException("Invalid expression");
                    }
                    BigDecimal divisor = stack.pop();
                    BigDecimal dividend = stack.pop();
                    if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                        throw new CalculatorException("Division by zero");
                    }
                    stack.push(dividend.divide(divisor, SCALE, RoundingMode.HALF_UP));
                    break;

                default:
                    throw new CalculatorException("Invalid expression");
            }
        }

        if (stack.size() != 1) {
            throw new CalculatorException("Invalid expression");
        }
        return stack.pop();
    }

    /**
     * 弹出两个操作数并执行二元运算；操作数不足则表达式无效。
     */
    private BigDecimal applyBinary(Deque<BigDecimal> stack,
                                   java.util.function.BiFunction<BigDecimal, BigDecimal, BigDecimal> op) {
        if (stack.size() < 2) {
            throw new CalculatorException("Invalid expression");
        }
        BigDecimal b = stack.pop();
        BigDecimal a = stack.pop();
        return op.apply(a, b);
    }
}
