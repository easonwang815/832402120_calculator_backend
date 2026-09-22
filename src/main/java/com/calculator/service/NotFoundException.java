package com.calculator.service;

/**
 * 资源不存在异常（用于历史记录删除时 id 不存在），全局处理器转 404。
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
