package com.calculator.model;

import java.util.List;

/** 历史记录搜索和分页结果。 */
public record HistoryPage(
        List<CalculationHistory> records,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
