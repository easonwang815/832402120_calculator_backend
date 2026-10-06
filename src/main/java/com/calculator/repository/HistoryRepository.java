package com.calculator.repository;

import com.calculator.model.CalculationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Reads and writes history records using Spring Data JPA. */
public interface HistoryRepository extends JpaRepository<CalculationHistory, Long> {

    Page<CalculationHistory> findByExpressionContainingIgnoreCaseOrResultContainingIgnoreCase(
            String expression, String result, Pageable pageable);
}
