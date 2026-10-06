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

/** Reads, searches and deletes history in the backend database. */
@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /** Searches expressions and results and returns the newest records first. */
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

    /** Deletes one record, or reports that the ID does not exist. */
    public void deleteById(Long id) {
        if (!historyRepository.existsById(id)) {
            throw new NotFoundException("Record not found");
        }
        historyRepository.deleteById(id);
    }

    /** Clears all history records. */
    public void clearAll() {
        historyRepository.deleteAll();
    }
}
