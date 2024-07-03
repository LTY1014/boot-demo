package com.lty.service;

import org.openjdk.jol.vm.VM;

import java.util.HashMap;

public class JolTest {

    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB", "PB"};


    public static void main(String[] args) {
        String str = "Hello World";
        long stringSize = getMemorySize(str);
        System.out.println("String size: " + stringSize + " B");
        System.out.println("String format size: " + formatFileSize(stringSize));

        HashMap<String, Integer> hashMap = new HashMap<>();

        // 让hashmap变成是个MB大小
        while (hashMap.size() < 1024 * 1024) {
            hashMap.put("Key" + hashMap.size(), hashMap.size());
        }
        hashMap.put("Key1", 1);
        hashMap.put("Key2", 2);
        long hashMapSize = getMemorySize(hashMap);
        System.out.println("HashMap size: " + hashMapSize + "B");
        System.out.println("HashMap format size: " + formatFileSize(hashMapSize));
    }

    public static long getMemorySize(Object obj){
        return VM.current().sizeOf(obj);
    }


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
        return String.format("%.1f%s", formattedSize, UNITS[digitGroups]);
    }
}