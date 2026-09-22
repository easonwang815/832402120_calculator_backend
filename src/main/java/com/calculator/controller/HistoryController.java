package com.calculator.controller;

import com.calculator.model.ApiResponse;
import com.calculator.service.HistoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 历史记录接口：
 * - GET    /api/history        查询全部历史（时间倒序）
 * - DELETE /api/history/{id}   删除指定记录
 * - DELETE /api/history        清空全部（加分项）
 */
@RestController
@RequestMapping("/api")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history")
    public ApiResponse listAll() {
        return ApiResponse.successData(historyService.listAll());
    }

    @DeleteMapping("/history/{id}")
    public ApiResponse deleteById(@PathVariable Long id) {
        historyService.deleteById(id);
        return ApiResponse.successData(true);
    }

    @DeleteMapping("/history")
    public ApiResponse clearAll() {
        historyService.clearAll();
        return ApiResponse.successData(true);
    }
}
