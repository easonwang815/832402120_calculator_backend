package com.calculator.controller;

import com.calculator.model.ApiResponse;
import com.calculator.service.HistoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** API endpoints for reading, deleting and clearing history. */
@RestController
@RequestMapping("/api")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history")
    public ApiResponse search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.successData(historyService.search(keyword, page, size));
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
