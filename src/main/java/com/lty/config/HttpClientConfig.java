package com.lty.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HttpClient连接池配置
 *
 * @author lty
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(200);                   // 最大总连接数
        manager.setDefaultMaxPerRoute(50);         // 每个域名最大并发连接数
        return manager;
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000) // 连接超时
                .setSocketTimeout(60000) // 读取超时
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .build();
    }
}