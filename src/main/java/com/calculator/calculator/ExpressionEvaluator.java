package com.calculator.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 表达式求值器：使用调度场算法将中缀表达式转为后缀表达式后求值。
 * 支持四则运算、一元正负号、次方以及 sin/cos/tan/sqrt 科学函数。
 * 三角函数使用角度制。
 */
public class ExpressionEvaluator {

    private static final int SCALE = 10;
    private static final int MAX_INTEGER_EXPONENT = 1000;
    private static final double ZERO_EPSILON = 1e-12;
    private static final String UNARY_MINUS = "-u";

    public String evaluate(String expression) {
        List<Token> tokens = new ExpressionParser().parse(expression);
        BigDecimal result = evaluatePostfix(toPostfix(tokens));
        String formatted = result.stripTrailingZeros().toPlainString();
        if (formatted.length() > 1024) {
            throw new CalculatorException("Result is too large");
        }
        return formatted;
    }

    private List<Token> toPostfix(List<Token> tokens) {
        Deque<Token> opStack = new ArrayDeque<>();
        List<Token> output = new ArrayList<>();
        boolean expectOperand = true;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (token.getType()) {
                case NUMBER:
                    if (!expectOperand) {
                        throw new CalculatorException("Invalid expression");
                    }
                    output.add(token);
                    expectOperand = false;
                    break;

                case FUNCTION:
                    if (!expectOperand || i + 1 >= tokens.size()
                            || tokens.get(i + 1).getType() != TokenType.LPAREN) {
                        throw new CalculatorException("Invalid expression");
                    }
                    opStack.push(token);
                    break;

                case PLUS:
                    if (expectOperand) {
                        break;
                    }
                    pushOperator(token, opStack, output);
                    expectOperand = true;
                    break;

                case MINUS:
                    if (expectOperand) {
                        opStack.push(new Token(TokenType.MINUS, UNARY_MINUS));
                        break;
                    }
                    pushOperator(token, opStack, output);
                    expectOperand = true;
                    break;

                case MULTIPLY:
                case DIVIDE:
                case POWER:
                    if (expectOperand) {
                        throw new CalculatorException("Invalid expression");
                    }
                    pushOperator(token, opStack, output);
                    expectOperand = true;
                    break;

                case LPAREN:
                    if (!expectOperand) {
                        throw new CalculatorException("Invalid expression");
                    }
                    opStack.push(token);
                    expectOperand = true;
                    break;

                case RPAREN:
                    if (expectOperand) {
                        throw new CalculatorException("Invalid expression");
                    }
                    popUntilLeftParenthesis(opStack, output);
                    if (!opStack.isEmpty() && opStack.peek().getType() == TokenType.FUNCTION) {
                        output.add(opStack.pop());
                    }
                    expectOperand = false;
                    break;

                default:
                    throw new CalculatorException("Invalid expression");
            }
        }

        if (expectOperand) {
            throw new CalculatorException("Invalid expression");
        }
        while (!opStack.isEmpty()) {
            Token top = opStack.pop();
            if (top.getType() == TokenType.LPAREN || top.getType() == TokenType.RPAREN
                    || top.getType() == TokenType.FUNCTION) {
                throw new CalculatorException("Invalid expression");
            }
            output.add(top);
        }
        return output;
    }

    private void pushOperator(Token operator, Deque<Token> opStack, List<Token> output) {
        while (!opStack.isEmpty() && isOperator(opStack.peek())
                && (precedence(opStack.peek()) > precedence(operator)
                || (!isRightAssociative(operator)
                && precedence(opStack.peek()) == precedence(operator)))) {
            output.add(opStack.pop());
        }
        opStack.push(operator);
    }

    private void popUntilLeftParenthesis(Deque<Token> opStack, List<Token> output) {
        while (!opStack.isEmpty() && opStack.peek().getType() != TokenType.LPAREN) {
            output.add(opStack.pop());
        }
        if (opStack.isEmpty()) {
            throw new CalculatorException("Invalid expression");
        }
        opStack.pop();
    }

    private boolean isOperator(Token token) {
        return token.getType() == TokenType.PLUS
                || token.getType() == TokenType.MINUS
                || token.getType() == TokenType.MULTIPLY
                || token.getType() == TokenType.DIVIDE
                || token.getType() == TokenType.POWER;
    }

    private boolean isRightAssociative(Token token) {
        return token.getType() == TokenType.POWER || UNARY_MINUS.equals(token.getValue());
    }

    private int precedence(Token token) {
        if (UNARY_MINUS.equals(token.getValue())) {
            return 3;
        }
        return switch (token.getType()) {
            case PLUS, MINUS -> 1;
            case MULTIPLY, DIVIDE -> 2;
            case POWER -> 4;
            default -> 0;
        };
    }

    private BigDecimal evaluatePostfix(List<Token> postfix) {
        Deque<BigDecimal> stack = new ArrayDeque<>();
        for (Token token : postfix) {
            switch (token.getType()) {
                case NUMBER:
                    stack.push(new BigDecimal(token.getValue()));
                    break;
                case PLUS:
                    stack.push(applyBinary(stack, BigDecimal::add));
                    break;
                case MINUS:
                    if (UNARY_MINUS.equals(token.getValue())) {
                        requireOperands(stack, 1);
                        stack.push(stack.pop().negate());
                    } else {
                        stack.push(applyBinary(stack, BigDecimal::subtract));
                    }
                    break;
                case MULTIPLY:
                    stack.push(applyBinary(stack, BigDecimal::multiply));
                    break;
                case DIVIDE:
                    stack.push(divide(stack));
                    break;
                case POWER:
                    stack.push(power(stack));
                    break;
                case FUNCTION:
                    requireOperands(stack, 1);
                    stack.push(applyFunction(token.getValue(), stack.pop()));
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

    private BigDecimal divide(Deque<BigDecimal> stack) {
        requireOperands(stack, 2);
        BigDecimal divisor = stack.pop();
        BigDecimal dividend = stack.pop();
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new CalculatorException("Division by zero");
        }
        return dividend.divide(divisor, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal power(Deque<BigDecimal> stack) {
        requireOperands(stack, 2);
        BigDecimal exponent = stack.pop();
        BigDecimal base = stack.pop();
        try {
            int integerExponent = exponent.intValueExact();
            if (Math.abs((long) integerExponent) > MAX_INTEGER_EXPONENT) {
                throw new CalculatorException("Result is too large");
            }
            if (integerExponent >= 0) {
                return base.pow(integerExponent, MathContext.DECIMAL128);
            }
            if (base.compareTo(BigDecimal.ZERO) == 0) {
                throw new CalculatorException("Division by zero");
            }
            return BigDecimal.ONE.divide(
                    base.pow(-integerExponent, MathContext.DECIMAL128),
                    SCALE,
                    RoundingMode.HALF_UP);
        } catch (ArithmeticException ignored) {
            return fromFiniteDouble(Math.pow(base.doubleValue(), exponent.doubleValue()));
        }
    }

    private BigDecimal applyFunction(String function, BigDecimal argument) {
        double value = argument.doubleValue();
        return switch (function) {
            case "sqrt" -> {
                if (argument.compareTo(BigDecimal.ZERO) < 0) {
                    throw new CalculatorException("Invalid function argument");
                }
                yield argument.sqrt(MathContext.DECIMAL128);
            }
            case "sin" -> fromFiniteDouble(Math.sin(Math.toRadians(value)));
            case "cos" -> fromFiniteDouble(Math.cos(Math.toRadians(value)));
            case "tan" -> {
                double radians = Math.toRadians(value);
                if (Math.abs(Math.cos(radians)) < ZERO_EPSILON) {
                    throw new CalculatorException("Invalid function argument");
                }
                yield fromFiniteDouble(Math.tan(radians));
            }
            default -> throw new CalculatorException("Invalid expression");
        };
    }

    private BigDecimal fromFiniteDouble(double value) {
        if (!Double.isFinite(value)) {
            throw new CalculatorException("Invalid function argument");
        }
        if (Math.abs(value) < ZERO_EPSILON) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value).setScale(12, RoundingMode.HALF_UP);
    }

    private BigDecimal applyBinary(Deque<BigDecimal> stack,
                                   BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        requireOperands(stack, 2);
        BigDecimal right = stack.pop();
        BigDecimal left = stack.pop();
        return operation.apply(left, right);
    }

    private void requireOperands(Deque<BigDecimal> stack, int count) {
        if (stack.size() < count) {
            throw new CalculatorException("Invalid expression");
        }
    }
}
