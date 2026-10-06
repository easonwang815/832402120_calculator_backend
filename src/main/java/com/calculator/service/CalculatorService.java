package com.calculator.service;

import com.calculator.calculator.CalculatorException;
import com.calculator.calculator.ExpressionEvaluator;
import com.calculator.model.CalculationHistory;
import com.calculator.repository.HistoryRepository;
import org.springframework.stereotype.Service;

/** Calculates an expression, saves a successful result and returns the answer. */
@Service
public class CalculatorService {

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    private final HistoryRepository historyRepository;

    public CalculatorService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /** Calculates and saves the result. Invalid calculations are not saved. */
    public String calculate(String expression) {
        String result = evaluator.evaluate(expression);
        historyRepository.save(new CalculationHistory(expression, result));
        return result;
    }
}
