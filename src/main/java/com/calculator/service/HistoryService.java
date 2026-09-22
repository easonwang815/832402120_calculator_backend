package com.calculator.service;

import com.calculator.model.CalculationHistory;
import com.calculator.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 查询全部历史，按时间倒序。
     */
    public List<CalculationHistory> listAll() {
        return historyRepository.findAllByOrderByCreatedAtDescIdDesc();
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
