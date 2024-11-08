package com.lty.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;

/**
 * 加密
 *
 * @author lty
 * @remarks: 配置文件密码加密 ENC(value)
 */
public class JasyptUtil {

    /**
     * 加密钥匙
     */
    @Value("${jasypt.encryptor.password}")
    public static String key = "lty";

    public static void main(String[] args) {
        // 加密 若修改了第一个参数加密password记得在配置文件同步修改
        System.out.println(encrypt(key, "123456"));
        // 解密
        System.out.println(decrypt(key, "fs0jGHuO1DbOEgiWmeXLWperUh7H2pXHJmIdIHb5FRbz3sVnR9zXXmqknu04J4qe"));
    }

    /**
     * Jasypt生成加密结果
     *
     * @param key   配置文件中设定的加密钥匙
     * @param value 待加密值
     * @return
     */
    public static String encrypt(String key, String value) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(encryptConfig(key));
        String result = encryptor.encrypt(value);
        return result;
    }

    /**
     * 解密
     *
     * @param key
     * @param value 待解密密文
     * @return
     */
    public static String decrypt(String key, String value) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(encryptConfig(key));
        encryptor.decrypt(value);
        String result = encryptor.decrypt(value);
        return result;
    }

    public static SimpleStringPBEConfig encryptConfig(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize(1);
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        return config;
    }
}
