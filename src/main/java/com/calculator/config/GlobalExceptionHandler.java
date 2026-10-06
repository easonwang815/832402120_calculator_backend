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

/** Turns request and server errors into HTTP status codes and JSON responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Invalid calculations return 400. */
    @ExceptionHandler(CalculatorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleCalculatorException(CalculatorException e) {
        return ApiResponse.error(e.getMessage());
    }

    /** Invalid request fields return 400. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Invalid request");
        return ApiResponse.error(message);
    }

    /** Invalid JSON or a missing body returns 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleUnreadableRequest(HttpMessageNotReadableException e) {
        return ApiResponse.error("Invalid request");
    }

    /** Invalid path parameter types return 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ApiResponse.error("Invalid request");
    }

    /** Unsupported content types return 415. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResponse handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        return ApiResponse.error("Unsupported media type");
    }

    /** Unsupported HTTP methods return 405. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse handleUnsupportedMethod(HttpRequestMethodNotSupportedException e) {
        return ApiResponse.error("Method not allowed");
    }

    /** Unknown API paths return 404. */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNoResource(NoResourceFoundException e) {
        return ApiResponse.error("Resource not found");
    }

    /** Missing history records return 404. */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNotFoundException(NotFoundException e) {
        return ApiResponse.error(e.getMessage());
    }

    /** Unexpected errors return 500 without internal details. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleGenericException(Exception e) {
        LOGGER.error("Unhandled server error", e);
        return ApiResponse.error("Internal server error");
    }
}
