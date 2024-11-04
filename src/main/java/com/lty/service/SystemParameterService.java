package com.lty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lty.model.entity.SystemParameter;

/**
 * @author lty
 */
public interface SystemParameterService extends IService<SystemParameter> {

    /**
     * 根据code获取参数值
     *
     * @param code       业务编码
     * @param targetType 值类型
     * @param <T>        目标类型
     * @return
     */
    <T> T getValue(String code, Class<T> targetType);
}