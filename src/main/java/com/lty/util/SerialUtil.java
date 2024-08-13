package com.lty.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description 序列号生成工具类
 * @author lty
 */
public class SerialUtil {
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static DecimalFormat numberFormat = new DecimalFormat("0000");
    private static ConcurrentMap<LocalDate, Integer> dailyCounters = new ConcurrentHashMap<>();

    public static String generateSerial() {
        LocalDate today = LocalDate.now();

        // 获取当天的计数器，并设置为1（如果不存在的话）
        int dailyNumber = dailyCounters.merge(today, 1, Integer::sum);
        // 格式化当日流水号，不足四位补零
        String dailySerialNumber = numberFormat.format(dailyNumber);
        // 拼接日期和流水号
        return today.format(DateTimeFormatter.ofPattern(DATE_FORMAT)) + dailySerialNumber;
    }
}
