package com.calculator.controller;

import com.calculator.model.ApiResponse;
import com.calculator.model.CalculateRequest;
import com.calculator.service.CalculatorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Receives expressions. Calculation and history saving happen in the backend. */
@RestController
@RequestMapping("/api")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/calculate")
    public ApiResponse calculate(@Valid @RequestBody CalculateRequest request) {
        String result = calculatorService.calculate(request.getExpression());
        return ApiResponse.success(request.getExpression(), result);
    }
}
