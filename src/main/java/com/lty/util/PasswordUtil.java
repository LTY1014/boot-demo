package com.lty.util;

import cn.hutool.crypto.SecureUtil;
import com.lty.constant.BaseConstant;

import java.util.Objects;
import java.util.Random;

/**
 * @author lty
 */
public class PasswordUtil {
    /**
     * 校验密码
     *
     * @param password       未加密密码
     * @param encodePassword 加密后的密码 -- 指数据库中存储的密码
     * @return true:正确 false:错误
     */
    public static boolean isValidPassword(String password, String encodePassword) {
        String decryptStr = SecureUtil.aes(BaseConstant.AES_KEY).decryptStr(encodePassword);
        return Objects.equals(password, decryptStr);
    }

    /**
     * 求加密后的密码
     *
     * @param password 密码
     * @return 加密后的密码
     */
    public static String encodePassword(String password) {
        return SecureUtil.aes(BaseConstant.AES_KEY).encryptHex(password);
    }

    /**
     * 解密
     *
     * @param encodePassword 加密后的密码
     * @return 加密后的密码
     */
    public static String decrypt(String encodePassword) {
        return SecureUtil.aes(BaseConstant.AES_KEY).decryptStr(encodePassword);
    }

    /**
     * 随机6位数生成
     */
    public static String getRandomNum() {

        Random random = new Random();
        int num = random.nextInt(999999);
        //不足六位前面补0
        String str = String.format("%06d", num);
        return str;
    }

    /**
     * 密码工具类测试
     * @author lty
     */
    public static void main(String[] args) {
        String pwd="123456";
        String encodePwd = PasswordUtil.encodePassword(pwd);
        System.out.println("原始密码为"+pwd);
        System.out.println("加密后为"+encodePwd);

        String decode=PasswordUtil.decrypt(encodePwd);
        System.out.println(decode);
    }
}
