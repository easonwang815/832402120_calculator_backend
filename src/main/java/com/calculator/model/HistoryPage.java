package com.calculator.model;

import java.util.List;

/** History records and page information returned by the API. */
public record HistoryPage(
        List<CalculationHistory> records,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
