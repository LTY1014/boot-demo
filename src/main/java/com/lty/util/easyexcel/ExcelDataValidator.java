package com.lty.util.easyexcel;

import com.lty.model.dto.ExcelDemo;
import lombok.Data;

/**
 * Excel数据校验器
 *
 * @author lty
 */
@Data
public class ExcelDataValidator implements DataValidator<ExcelDemo> {

    @Override
    public void validate(ExcelDemo data) {
        // 添加校验逻辑
        if (data.getCode() == null || data.getCode().isEmpty()) {
            throw new IllegalArgumentException("field cannot be null or empty");
        }
    }
}