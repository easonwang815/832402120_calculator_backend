package com.calculator.repository;

import com.calculator.model.CalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 历史记录数据访问层。Spring Data JPA 自动实现增删查。
 */
public interface HistoryRepository extends JpaRepository<CalculationHistory, Long> {

    /**
     * 按时间倒序查询全部历史（最新的在最前）。
     */
    List<CalculationHistory> findAllByOrderByCreatedAtDesc();
}
