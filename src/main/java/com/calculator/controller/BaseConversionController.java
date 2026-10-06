package com.calculator.controller;

import com.calculator.model.ApiResponse;
import com.calculator.model.BaseConversionRequest;
import com.calculator.service.BaseConversionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API endpoint for number-base conversion. */
@RestController
@RequestMapping("/api")
public class BaseConversionController {

    private final BaseConversionService conversionService;

    public BaseConversionController(BaseConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert")
    public ApiResponse convert(@Valid @RequestBody BaseConversionRequest request) {
        return ApiResponse.successData(conversionService.convert(
                request.getValue(), request.getFromBase(), request.getToBase()));
    }
}
