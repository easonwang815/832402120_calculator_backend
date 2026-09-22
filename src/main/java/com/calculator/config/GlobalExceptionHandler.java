package com.calculator.config;

import com.calculator.calculator.CalculatorException;
import com.calculator.model.ApiResponse;
import com.calculator.service.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理：把异常统一转换为 400/404/500 + 统一 JSON 响应。
 * Controller 无需各自 try-catch，逻辑更干净。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    /** JSON 无法解析或缺少请求体 → 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleUnreadableRequest(HttpMessageNotReadableException e) {
        return ApiResponse.error("Invalid request");
    }

    /** 路径参数类型错误（如 /history/abc）→ 400 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ApiResponse.error("Invalid request");
    }

    /** 请求媒体类型不受支持 → 415 */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResponse handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        return ApiResponse.error("Unsupported media type");
    }

    /** 请求方法不受支持 → 405 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse handleUnsupportedMethod(HttpRequestMethodNotSupportedException e) {
        return ApiResponse.error("Method not allowed");
    }

    /** API 路径不存在 → 404 */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNoResource(NoResourceFoundException e) {
        return ApiResponse.error("Resource not found");
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
        LOGGER.error("Unhandled server error", e);
        return ApiResponse.error("Internal server error");
    }
}
