package com.calculator.calculator;

/** Reports a calculation error to the API error handler. */
public class CalculatorException extends RuntimeException {

    public CalculatorException(String message) {
        super(message);
    }
}
