package com.lty.util.easyexcel;

/**
 * 数据校验器接口
 * @param <T>
 */
public interface DataValidator<T> {
    void validate(T data);
}