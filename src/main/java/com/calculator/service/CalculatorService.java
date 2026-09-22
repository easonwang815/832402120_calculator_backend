package com.calculator.service;

import com.calculator.calculator.CalculatorException;
import com.calculator.calculator.ExpressionEvaluator;
import com.calculator.model.CalculationHistory;
import com.calculator.repository.HistoryRepository;
import org.springframework.stereotype.Service;

/**
 * 计算服务：核心流程编排。
 * 前端只传入表达式 → 后端解析计算 → 成功后写入历史 → 返回结果。
 * 核心计算逻辑在 ExpressionEvaluator（纯 Java，不依赖 Spring）。
 */
@Service
public class CalculatorService {

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    private final HistoryRepository historyRepository;

    public CalculatorService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * 计算表达式并保存历史。
     *
     * @param expression 表达式原文
     * @return 格式化后的计算结果
     * @throws CalculatorException 表达式无效或除零（不会写入历史）
     */
    public String calculate(String expression) {
        String result = evaluator.evaluate(expression);
        historyRepository.save(new CalculationHistory(expression, result));
        return result;
    }
}
