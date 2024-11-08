package com.lty.util;

import cn.hutool.crypto.digest.DigestUtil;

import java.io.InputStream;

/**
 * 文件大小工具类
 * @author lty
 */
public class BusinessFileUtil {

    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB", "PB"};

    /**
     * 将字节数转换为可读的文件大小格式
     * @param size 字节数
     * @return 可读的文件大小格式
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }

        // 计算单位
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        // 防止由于数字过大导致digitGroups超出UNITS数组的范围
        digitGroups = Math.min(digitGroups, UNITS.length - 1);
        double formattedSize = size / Math.pow(1024, digitGroups);
        // String.format用于格式化输出，保证结果保留1位小数
        return String.format("%.1f %s", formattedSize, UNITS[digitGroups]);
    }

    /**
     * 获取文件MD5
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static String getFileMd5(InputStream inputStream) throws Exception {
        return DigestUtil.md5Hex(inputStream);
    }

    public static void main(String[] args) {
        System.out.println(formatFileSize(1024)); // 1.0 KB
        System.out.println(formatFileSize(1048574)); // 1.0 MB
        System.out.println(formatFileSize(1073741824)); // 1.0 GB
        System.out.println(formatFileSize(123456789)); // 117.7 MB
    }
}
