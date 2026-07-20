package com.lty.util;

import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    /**
     * 公共工具：将MultipartFile写入本地目标文件
     * @param multipartFile 前端上传文件
     * @param targetFile 本地保存文件对象
     */
    public static void saveMultipartFileToLocal(MultipartFile multipartFile, File targetFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 父目录不存在则创建
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            // 创建文件（不存在才新建，已存在会覆盖）
            targetFile.createNewFile();
            // 文件落地
            multipartFile.transferTo(targetFile);
        } catch (Exception e) {
            throw new RuntimeException("文件保存至本地失败，目标路径:" + targetFile.getAbsolutePath(), e);
        }
    }

    public static void main(String[] args) {
        System.out.println(formatFileSize(1024)); // 1.0 KB
        System.out.println(formatFileSize(1048574)); // 1.0 MB
        System.out.println(formatFileSize(1073741824)); // 1.0 GB
        System.out.println(formatFileSize(123456789)); // 117.7 MB
    }
}
