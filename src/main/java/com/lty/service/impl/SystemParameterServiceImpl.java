package com.lty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lty.mapper.SystemParameterMapper;
import com.lty.model.entity.SystemParameter;
import com.lty.service.SystemParameterService;
import com.lty.util.ConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lty
 */
@Slf4j
@Service
public class SystemParameterServiceImpl extends ServiceImpl<SystemParameterMapper, SystemParameter> implements SystemParameterService {

    @Resource
    private SystemParameterMapper systemParameterMapper;

    @Override
    public <T> T getValue(String code, Class<T> targetType) {
        if (code == null || targetType == null) {
            throw new IllegalArgumentException("Code and targetType cannot be null");
        }
        LambdaQueryWrapper<SystemParameter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemParameter::getCode, code).last("limit 1");

        SystemParameter systemParameter = this.getOne(queryWrapper);
        if (systemParameter == null || systemParameter.getValue() == null) {
            return null;
        }
        return ConversionUtil.convert(systemParameter.getValue(), targetType);
    }
}