package com.lty.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class BigDecimalUtil {

    public final static BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * 检查列表中的总和是否等于 100。
     *
     * @param list 包含 BigDecimal 的列表
     * @return 如果总和等于 100 返回 true，否则返回 false
     */
    public static Boolean isHundred(List<BigDecimal> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        // 计算总值
        BigDecimal sum = list.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.compareTo(HUNDRED) == 0;
    }

    /**
     * 检查两个 BigDecimal 的和是否等于 100。
     *
     * @param bigDecimal1 第一个 BigDecimal
     * @param bigDecimal2 第二个 BigDecimal
     * @return 如果两个值的和等于 100 返回 true，否则返回 false
     */
    public static Boolean isHundred(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        Objects.requireNonNull(bigDecimal1, "bigDecimal1 cannot be null");
        Objects.requireNonNull(bigDecimal2, "bigDecimal2 cannot be null");

        BigDecimal sum = bigDecimal1.add(bigDecimal2);
        return sum.compareTo(HUNDRED) == 0;
    }
}
