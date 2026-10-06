package com.calculator.model;

/** The result of a number-base conversion. */
public record BaseConversionResult(String value, int fromBase, int toBase, String result) {
}
