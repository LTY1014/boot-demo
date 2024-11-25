package com.lty.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExcelDemo {

    @ExcelProperty("id")
    public String id;

    @ExcelProperty("姓名")
    public String name;

    @ExcelProperty("编码")
    public String code;
}
