package com.lty.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES加解密 与前端互通
 */
public class AesUtil {

    // ====前后端统一===
    // key固定
    private static final String KEY = "1234567890abcdef";
    // IV最好随机生成
    private static final String IV = "abcdef1234567890";
    // 算法
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * 加密 返回十六进制Hex (格式: IV(32字符) + 密文)
     */
    public static String encrypt(String content) {
        return encrypt(content, null);
    }

    /**
     * 加密 返回十六进制Hex (格式: IV(32字符) + 密文)
     * @param iv 可选，传入null则生成随机IV
     */
    public static String encrypt(String content, String iv) {
        try {
            // 使用传入的IV或生成随机IV
            byte[] ivBytes;
            if (iv != null && !iv.isEmpty()) {
                ivBytes = iv.getBytes(StandardCharsets.UTF_8);
            } else {
                ivBytes = generateRandomIV();
            }

            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryptBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

            // 将IV和密文拼接: IV(32字符) + 密文
            return bytesToHex(ivBytes) + bytesToHex(encryptBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密 传入Hex字符串 (格式: IV(32字符) + 密文)
     */
    public static String decrypt(String hexStr) {
        try {
            if (hexStr == null || hexStr.length() < 32) {
                return null;
            }

            // 提取IV (前32个字符 = 16字节)
            String ivHex = hexStr.substring(0, 32);
            String cipherHex = hexStr.substring(32);

            byte[] ivBytes = hexToBytes(ivHex);
            byte[] cipherBytes = hexToBytes(cipherHex);

            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptBytes = cipher.doFinal(cipherBytes);
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 生成随机16字节IV
    private static byte[] generateRandomIV() {
        byte[] iv = new byte[16];
        new java.security.SecureRandom().nextBytes(iv);
        return iv;
    }

    // 字节数组转16进制
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    // 16进制转字节数组
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    // 测试
    public static void main(String[] args) {
        String text = "123456";

        // 测试1: 随机IV加密解密
        System.out.println("=== 测试1: 随机IV加密解密 ===");
        String en = encrypt(text);
        System.out.println("加密结果: " + en);
        String de = decrypt(en);
        System.out.println("解密结果: " + de);

        // 测试2: 固定IV加密解密（兼容旧数据）
        System.out.println("\n=== 测试2: 固定IV加密解密 ===");
        String enFixed = encrypt(text, IV);
        System.out.println("固定IV加密: " + enFixed);
        String deFixed = decrypt(enFixed);
        System.out.println("解密结果: " + deFixed);

        // 测试3: 模拟前端传来的数据
        System.out.println("\n=== 测试3: 模拟前端数据 ===");
        String mockFrontendData = encrypt("123456");
        System.out.println("模拟前端加密: " + mockFrontendData);
        System.out.println("后端解密: " + decrypt(mockFrontendData));
    }
}