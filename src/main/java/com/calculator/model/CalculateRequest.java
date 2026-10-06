package com.calculator.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** An expression sent by the frontend, such as (1+2)*3. */
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
