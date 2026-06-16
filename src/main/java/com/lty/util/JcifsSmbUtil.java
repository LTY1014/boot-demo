package com.lty.util;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * jcifs SMB工具类
 */
public class JcifsSmbUtil {

    /**
     * 获取认证对象
     * @param domain 工作组填空字符串""，域环境填域名
     * @param username Windows登录账号
     * @param password Windows密码
     * @return NtlmPasswordAuthentication
     */
    public static NtlmPasswordAuthentication getAuth(String domain, String username, String password) {
        return new NtlmPasswordAuthentication(domain, username, password);
    }

    /**
     * 遍历共享文件夹下所有文件
     * @param smbUrl 共享路径格式：smb://192.168.1.100/共享文件夹名/
     * @param auth 账号密码凭证
     * @throws IOException
     */
    public static void listSmbDir(String smbUrl, NtlmPasswordAuthentication auth) throws IOException {
        SmbFile smbDir = new SmbFile(smbUrl, auth);
        if (!smbDir.exists()) {
            System.out.println("共享目录不存在：" + smbUrl);
            return;
        }
        if (!smbDir.isDirectory()) {
            System.out.println("路径不是文件夹");
            return;
        }
        SmbFile[] files = smbDir.listFiles();
        for (SmbFile file : files) {
            System.out.printf("文件名：%s，大小：%d byte%n", file.getName(), file.length());
        }
    }

    /**
     * 获取共享文件输入流，直接读取文件内容
     * @param smbFileUrl smb完整文件路径
     * @param auth 认证凭证
     * @return 文件输入流
     * @throws IOException
     */
    public static InputStream getFileInputStream(String smbFileUrl, NtlmPasswordAuthentication auth) throws IOException {
        SmbFile smbFile = new SmbFile(smbFileUrl, auth);
        if (!smbFile.exists() || smbFile.isDirectory()) {
            throw new IOException("目标不存在或为文件夹：" + smbFileUrl);
        }
        return new SmbFileInputStream(smbFile);
    }

    /**
     * 下载共享文件到本地磁盘
     * @param smbFilePath smb文件地址
     * @param localSavePath 本地保存路径
     * @param auth 认证凭证
     * @throws IOException
     */
    public static void downloadFileToLocal(String smbFilePath, String localSavePath, NtlmPasswordAuthentication auth) throws IOException {
        try (InputStream in = getFileInputStream(smbFilePath, auth);
             FileOutputStream out = new FileOutputStream(localSavePath)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
    }

    // 测试示例
    public static void main(String[] args) {
        // 服务端IP
        String ip = "192.168.1.100";
        // 工作组电脑domain填空，域环境填写域名
        String domain = "";
        String user = "Administrator";
        String pwd = "123456";
        // 共享文件夹路径
        String sharePath = "smb://" + ip + "/共享文件/";
        // 需要读取的文件
        String targetFile = "smb://" + ip + "/共享文件/test.txt";

        try {
            NtlmPasswordAuthentication auth = getAuth(domain, user, pwd);
            // 1. 遍历目录
            listSmbDir(sharePath, auth);
            // 2. 下载文件到本地
            downloadFileToLocal(targetFile, "D:/test.txt", auth);
            System.out.println("文件下载完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}