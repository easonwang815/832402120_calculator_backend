package com.calculator.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Input for a number-base conversion. */
public class BaseConversionRequest {

    @NotBlank(message = "value must not be blank")
    @Size(max = 1024, message = "value too long")
    private String value;

    @Min(value = 2, message = "invalid source base")
    @Max(value = 16, message = "invalid source base")
    private int fromBase;

    @Min(value = 2, message = "invalid target base")
    @Max(value = 16, message = "invalid target base")
    private int toBase;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFromBase() {
        return fromBase;
    }

    public void setFromBase(int fromBase) {
        this.fromBase = fromBase;
    }

    public int getToBase() {
        return toBase;
    }

    public void setToBase(int toBase) {
        this.toBase = toBase;
    }
}
