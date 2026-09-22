package com.calculator.calculator;

/**
 * 计算异常：无效表达式 / 除零等。
 * Controller 层的全局异常处理器会将 message 返回给前端。
 */
public class CalculatorException extends RuntimeException {

    public CalculatorException(String message) {
        super(message);
    }
}
