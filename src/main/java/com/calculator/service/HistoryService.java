package com.calculator.service;

import com.calculator.calculator.CalculatorException;
import com.calculator.model.CalculationHistory;
import com.calculator.model.HistoryPage;
import com.calculator.repository.HistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * 历史记录服务：查询 / 删除 / 清空。
 * 数据一律来自后端数据库。
 */
@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /** 按关键词搜索表达式/结果，并按时间倒序分页。 */
    public HistoryPage search(String keyword, int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new CalculatorException("Invalid pagination parameters");
        }
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (normalizedKeyword.length() > 100) {
            throw new CalculatorException("Search keyword too long");
        }
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
        Page<CalculationHistory> result = normalizedKeyword.isEmpty()
                ? historyRepository.findAll(pageable)
                : historyRepository
                .findByExpressionContainingIgnoreCaseOrResultContainingIgnoreCase(
                        normalizedKeyword, normalizedKeyword, pageable);
        return new HistoryPage(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    /**
     * 删除指定 id 的历史记录；不存在时抛 NotFoundException（全局处理器转 404）。
     */
    public void deleteById(Long id) {
        if (!historyRepository.existsById(id)) {
            throw new NotFoundException("Record not found");
        }
        historyRepository.deleteById(id);
    }

    /**
     * 清空全部历史（加分项）。
     */
    public void clearAll() {
        historyRepository.deleteAll();
    }
}
