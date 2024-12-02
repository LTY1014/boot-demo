package com.lty.util;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * 获取IP信息
 *
 * @author lty
 */
@Slf4j
@Component
public class IpInfoUtil {
    static final String UNKNOWN = "unknown";

    /**
     * 获取客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 获取客户端IP地址
     *
     * @param ip
     * @return
     */
    public static String getClient(String ip) {
        // 1、使用 ClassPathResource 获取资源文件
        ClassPathResource resource = new ClassPathResource("static/ip2region.xdb");
        Searcher searcher = null;
        try (InputStream inputStream = resource.getInputStream()) {
            // 2、根据 InputStream 创建 Searcher
            byte[] dbBytes = inputStream.readAllBytes();
            searcher = Searcher.newWithBuffer(dbBytes);
        } catch (IOException e) {
            log.info("failed to create searcher: %s", e);
        }
        // 2、查询
        try {
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            log.info("region: {}, ioCount: {}, took: {} μs", region, searcher.getIOCount(), cost);
            String[] split = region.split("\\|");
            return !"0".equals(split[2]) ? split[2] : split[0];
        } catch (Exception e) {
            System.out.printf("failed to search(%s): %s\n", ip, e);
        }
        // 3、关闭资源
        try {
            searcher.close();
        } catch (IOException e) {
            log.info("failed to close: %s", e);
        }
        return "";
    }
}
