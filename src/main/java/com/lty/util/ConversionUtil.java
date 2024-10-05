package com.lty.util;

import java.math.BigDecimal;

/**
 * 类型转换工具类
 */
public class ConversionUtil {

    /**
     * 类型转换
     *
     * @param value
     * @param targetType
     * @param <T>
     * @return
     */
    public static <T> T convert(String value, Class<T> targetType) {
        if (targetType == Boolean.class) {
            return targetType.cast(Boolean.parseBoolean(value));
        } else if (targetType == Integer.class) {
            return targetType.cast(Integer.parseInt(value));
        } else if (targetType == Double.class) {
            return targetType.cast(Double.parseDouble(value));
        } else if (targetType == Long.class) {
            return targetType.cast(Long.parseLong(value));
        } else if (targetType == BigDecimal.class) {
            return targetType.cast(new BigDecimal(value));
        }
        throw new IllegalArgumentException("Unsupported type: " + targetType);
    }

    public static void main(String[] args) {
        String booleanValue = "false";
        boolean isTrue = ConversionUtil.convert(booleanValue, Boolean.class);
        System.out.println(isTrue);

        String intValue = "1";
        int intValue1 = ConversionUtil.convert(intValue, Integer.class);
        System.out.println(intValue1);
    }
}
