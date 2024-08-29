package com.lty.util;

import org.springframework.util.Base64Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 签名工具类
 * @author lty
 */
public class SignatureUtil {
    public static final int SIGN_LENGTH = 16;
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * 生成一个16位纯字母的随机密钥
     * @return
     */
    public static String generateRandomKey() {
        SecureRandom secureRandom = new SecureRandom();
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(SIGN_LENGTH);
        for (int i = 0; i < SIGN_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * 生成签名
     * @param data 需要签名的数据
     * @param secretKey 签名密钥
     * @return 签名结果
     */
    public static String generateSignature(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(data.getBytes());
            return Base64Utils.encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate signature");
        }
    }

    /**
     * 验证签名
     * @param data 需要验证的数据
     * @param signature 签名结果
     * @param secretKey 签名密钥
     * @return 签名是否验证通过
     */
    public static boolean verifySignature(String data, String signature, String secretKey) {
        String generatedSignature = generateSignature(data, secretKey);
        return generatedSignature.equals(signature);
    }

    public static void main(String[] args) {
        String content = "hello";
        // 生成随机密钥k
        String secretKey = SignatureUtil.generateRandomKey();
        // 输出密钥(如密钥为:tljXVfDBPqwdSFIM)
        System.out.println("密钥为:" + secretKey);
        // 生成签名(如签名结果为:Kv9HiWCWZmeJLhR1E0+pSAkwg4/mYJbmTvjoYaY1cEU=)
        String signContent = SignatureUtil.generateSignature(content, secretKey);
        System.out.println(signContent);
        System.out.println("-----------");
        System.out.println(SignatureUtil.verifySignature(content, signContent, secretKey));
    }
}
