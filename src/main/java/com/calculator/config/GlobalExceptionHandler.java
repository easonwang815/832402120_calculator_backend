package com.calculator.config;

import com.calculator.calculator.CalculatorException;
import com.calculator.model.ApiResponse;
import com.calculator.service.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：把异常统一转换为 400/404/500 + 统一 JSON 响应。
 * Controller 无需各自 try-catch，逻辑更干净。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 表达式无效 / 除零：客户端请求问题 → 400 */
    @ExceptionHandler(CalculatorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleCalculatorException(CalculatorException e) {
        return ApiResponse.error(e.getMessage());
    }

    /** 参数校验失败（expression 为空、超长等）→ 400 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Invalid request");
        return ApiResponse.error(message);
    }

    /** 历史记录不存在 → 404 */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNotFoundException(NotFoundException e) {
        return ApiResponse.error(e.getMessage());
    }

    /** 其他未预期异常 → 500，不向客户端泄露内部细节 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleGenericException(Exception e) {
        return ApiResponse.error("Internal server error");
    }
}
