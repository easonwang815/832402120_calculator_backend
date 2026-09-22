package com.calculator.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一 API 响应。
 * - 计算成功：{ success: true, expression, result }
 * - 计算失败：{ success: false, message }
 * - 列表成功：{ success: true, data: [...] }
 * 为 null 的字段不参与 JSON 序列化，保证响应干净。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private boolean success;
    private String expression;
    private String result;
    private String message;
    private Object data;

    private ApiResponse(boolean success) {
        this.success = success;
    }

    public static ApiResponse success(String expression, String result) {
        ApiResponse response = new ApiResponse(true);
        response.expression = expression;
        response.result = result;
        return response;
    }

    public static ApiResponse successData(Object data) {
        ApiResponse response = new ApiResponse(true);
        response.data = data;
        return response;
    }

    public static ApiResponse error(String message) {
        ApiResponse response = new ApiResponse(false);
        response.message = message;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
