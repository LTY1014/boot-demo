package com.lty.service.java;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 */
public class BigDecimalTest {

    @Test
    public void test(){
        // =================创建对象=================
        // 使用字符串初始化
        BigDecimal str = new BigDecimal("13.14");
        // 使用基本类型初始化
        BigDecimal number=BigDecimal.valueOf(13.14);
        // 初始化为零
        BigDecimal result = BigDecimal.ZERO;

        // =================计算公式====================
        BigDecimal anotherNumber=BigDecimal.valueOf(2);
        BigDecimal sum = number.add(anotherNumber);  // 加法
        BigDecimal difference = number.subtract(anotherNumber);  // 减法
        BigDecimal product = number.multiply(anotherNumber);  // 乘法
        BigDecimal quotient = number.divide(anotherNumber, RoundingMode.HALF_UP);  // 除法，使用指定的舍入模式
        BigDecimal remainder = number.remainder(anotherNumber);  // 取余数
        BigDecimal power = number.pow(2);  // 幂运算

        // =====================舍入和精度控制=========================
        BigDecimal rounded = number.setScale(2, RoundingMode.HALF_UP); // 设置小数点后保留两位数，并使用指定的舍入模式
        BigDecimal truncated = number.setScale(0, RoundingMode.DOWN);  // 去掉小数部分，保留整数部分
        BigDecimal max = number.max(anotherNumber);  // 返回两个数中较大的一个
        BigDecimal min = number.min(anotherNumber);  // 返回两个数中较小的一个
        BigDecimal stripTrailingZeros = number.stripTrailingZeros(); // 去掉小数点右侧的0
        String toPlainString = number.toPlainString();  // 返回一个字符串，但不包含指数部分(避免科学计数法)

        // ================比较和相等性判断===============
        int comparisonResult = number.compareTo(anotherNumber);  // 返回 -1、0 或 1，表示当前数与另一个数的大小关系
        boolean isEqual = number.equals(anotherNumber);  // 判断两个数是否完全相等
        boolean isGreaterThan = number.compareTo(anotherNumber) > 0;  // 判断当前数是否大于另一个数
        boolean isLessThan = number.compareTo(anotherNumber) < 0;  // 判断当前数是否小于另一个数

    }
}
