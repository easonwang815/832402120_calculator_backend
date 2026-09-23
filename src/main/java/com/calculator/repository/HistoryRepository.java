package com.calculator.repository;

import com.calculator.model.CalculationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 历史记录数据访问层。Spring Data JPA 自动实现增删查。
 */
public interface HistoryRepository extends JpaRepository<CalculationHistory, Long> {

    Page<CalculationHistory> findByExpressionContainingIgnoreCaseOrResultContainingIgnoreCase(
            String expression, String result, Pageable pageable);
}
