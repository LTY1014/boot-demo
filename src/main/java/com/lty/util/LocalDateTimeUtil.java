package com.lty.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime工具类
 * @author lty
 */
public class LocalDateTimeUtil {

    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 将LocalDateTime转换为Long
     * @param dateTime
     * @return
     */
    public static Long getLong(LocalDateTime dateTime) {
        long epochMilli = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return epochMilli;
    }

    /**
     * 将String转换为LocalDateTime
     * @param dateTimeString
     * @return
     */
    public static LocalDateTime getLocalDateTime(String dateTimeString) {
        LocalDateTime strToLocalDateTime = getLocalDateTime(dateTimeString, DATE_TIME_FORMATTER);
        return strToLocalDateTime;
    }

    /**
     * 将String转换为LocalDateTime
     * @param dateTimeString
     * @param formatter
     * @return
     */
    public static LocalDateTime getLocalDateTime(String dateTimeString, DateTimeFormatter formatter) {
        LocalDateTime strToLocalDateTime = LocalDateTime.parse(dateTimeString, formatter);
        return strToLocalDateTime;
    }

    /**
     * 将Long转换为LocalDateTime
     * @param epochMilli
     * @return
     */
    public static LocalDateTime getLocalDateTime(Long epochMilli) {
        LocalDateTime longToLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        return longToLocalDateTime;
    }

    /**
     * 将LocalDateTime转换为String
     * @param localDateTime
     * @return
     */
    public static String getString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER).toString();
    }

    /**
     * 将LocalDate转换为String
     * @param localDate
     * @return
     */
    public static String getString(LocalDate localDate) {
        return localDate.format(MONTH_FORMATTER).toString();
    }

    /**
     * 将月String转换为LocalDate
     * @param dateString
     * @return
     */
    public static LocalDate getLocalDate(String dateString) {
        YearMonth yearMonth = YearMonth.parse(dateString);
        return yearMonth.atDay(1);
    }

    /**
     * 判断两个日期是否在同一季度
     * @param source
     * @param target
     * @return
     */
    public static boolean areInSameQuarter(LocalDate source, LocalDate target) {
        // 获取两个日期的年份
        int year1 = source.getYear();
        int year2 = target.getYear();

        // 获取两个日期所属的季度
        int quarter1 = getQuarter(source);
        int quarter2 = getQuarter(target);

        // 判断年份和季度是否相同
        return year1 == year2 && quarter1 == quarter2;
    }

    /**
     * 获取季度
     * @param date
     * @return
     */
    public static int getQuarter(LocalDate date) {
        int month = date.getMonthValue();

        if (month >= 1 && month <= 3) {
            return 1;
        } else if (month >= 4 && month <= 6) {
            return 2;
        } else if (month >= 7 && month <= 9) {
            return 3;
        } else if (month >= 10 && month <= 12) {
            return 4;
        } else {
            throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    /**
     * 判断时间是否重叠
     * @param start1
     * @param end1
     * @param start2
     * @param end2
     * @return boolean 重叠为true,不重叠为false
     */
    public static boolean areRangesOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }
}
