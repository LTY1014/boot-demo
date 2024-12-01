package com.lty.util.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * EasyExcel读取监听器
 *
 * @param <T>
 * @example ExcelListener excelListener = new ExcelListener(new ExcelDataValidator(), list -> MyService.batchSave(list));
 */
@Slf4j
public class ExcelListener<T> implements ReadListener<T> {

    private static final int BATCH_COUNT = 1000; // 批量处理阈值
    private final List<T> list = new ArrayList<>(); // 数据缓存列表
    private final AtomicInteger totalCount = new AtomicInteger(0); // 数据总条数（线程安全）

    private DataValidator<T> validator;   // 数据校验器
    private Consumer<List<T>> processFunction; // 批量处理函数

    /**
     * 读每一条数据都会调用
     */
    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        list.add(data);
        totalCount.incrementAndGet(); // 增加总条数计数
        log.info("invoke data:{}", JSON.toJSONString(data));

        // 调用校验器进行校验
        if (validator != null) {
            validator.validate(data);
        }
        // 当数据量达到批次大小时，处理数据并清空缓存
        if (list.size() >= BATCH_COUNT) {
            processBatch();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("finish data size:{}", totalCount);
    }

    /**
     * 返回list
     */
    public List<T> getData() {
        return this.list;
    }

    /**
     * 处理当前批量数据
     */
    private void processBatch() {
        if (!list.isEmpty() && processFunction != null) {
            try {
                processFunction.accept(new ArrayList<>(list)); // 传递副本，避免并发问题
                log.info("Processed batch of size: {}", list.size());
            } catch (Exception e) {
                log.error("Error processing batch", e);
                throw new RuntimeException("Error processing batch", e);
            } finally {
                list.clear(); // 清空缓存数据
            }
        }
    }

    // region 构造函数start

    /**
     * 默认构造函数
     */
    public ExcelListener() {
    }

    /**
     * 构造函数，允许传入校验器和处理函数
     *
     * @param validator       数据校验器
     * @param processFunction 批量处理函数
     */
    public ExcelListener(DataValidator<T> validator, Consumer<List<T>> processFunction) {
        this.validator = validator;
        this.processFunction = processFunction;
    }

    /**
     * 构造函数，仅传入校验器
     *
     * @param validator 数据校验器
     */
    public ExcelListener(DataValidator<T> validator) {
        this.validator = validator;
    }

    /**
     * 设置数据校验器
     *
     * @param validator 数据校验器
     */
    public void setDataValidator(DataValidator<T> validator) {
        this.validator = validator;
    }

    /**
     * 设置批量处理函数
     *
     * @param processFunction 批量处理函数
     */
    public void setProcessFunction(Consumer<List<T>> processFunction) {
        this.processFunction = processFunction;
    }
    // endregion 构造函数end
}