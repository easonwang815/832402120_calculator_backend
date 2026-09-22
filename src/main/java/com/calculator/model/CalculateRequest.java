package com.calculator.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 计算请求体：{ "expression": "(1+2)*3" }
 */
public class CalculateRequest {

    @NotBlank(message = "expression must not be blank")
    @Size(max = 255, message = "expression too long")
    private String expression;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
