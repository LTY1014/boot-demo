package com.lty.util.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelListener<T> implements ReadListener<T> {

    public List<T> list = new ArrayList<>();
    public DataValidator<T> validator;

    // 构造函数，允许传入校验器
    public ExcelListener(DataValidator<T> validator) {
        this.validator = validator;
    }

    // setter 方法，允许后续设置校验器
    public void setDataValidator(DataValidator<T> validator) {
        this.validator = validator;
    }


    /**
     * 读每一条数据都会调用
     */
    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        list.add(data);
        log.info("invoke data:{}", JSON.toJSONString(data));

        // 调用校验器进行校验
        if (validator != null) {
            validator.validate(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("finish data size:{}", list.size());
    }

    /**
     * 返回list
     */
    public List<T> getData() {
        return this.list;
    }
}