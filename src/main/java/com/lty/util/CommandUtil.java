package com.lty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 命令工具类
 *
 * @author lty
 */
public class CommandUtil {
    private static final Logger log = LoggerFactory.getLogger(CommandUtil.class);

    /**
     * 执行系统命令/脚本
     *
     * @param command 命令列表（如执行shell脚本：Arrays.asList("/bin/bash", "/opt/test.sh")）
     * @param workDir 命令执行的工作目录（可为null，使用当前目录）
     * @return 命令执行结果（stdout）
     * @throws IOException          执行异常
     * @throws InterruptedException 线程中断异常
     */
    public static String executeCommand(List<String> command, String workDir)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        if (workDir != null && !workDir.isEmpty()) {
            processBuilder.directory(new java.io.File(workDir));
        }
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        long timeoutSeconds = 60;  // 设置超时时间为60秒
        // 等待指定时间或直到进程结束
        boolean finished = process.waitFor(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

        if (!finished) {
            // 超时则销毁进程
            process.destroyForcibly();
            throw new RuntimeException("命令执行超时");
        }

        StringBuilder output = new StringBuilder();
        try (InputStream inputStream = process.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.exitValue();
        log.info("命令执行完成，退出码：{}，输出：{}", exitCode, output);
        if (exitCode != 0) {
            throw new RuntimeException("命令执行失败，退出码：" + exitCode + "，输出：" + output);
        }
        return output.toString();
    }

    // 重载方法：默认工作目录
    public static String executeCommand(List<String> command) throws IOException, InterruptedException {
        return executeCommand(command, null);
    }
}