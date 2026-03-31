package com.lty.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 */
@Configuration
public class CacheConfig {
    // maximumSize=100000：最大容量10万条目,expireAfterWrite=1h：写入后1小时过期,recordStats：记录统计信息
    @Value("${cache.caffeine.spec:maximumSize=100000,expireAfterWrite=1h,recordStats}")
    private String cacheSpec;

    @Bean("localUrlCache")
    public Cache<String, String> localUrlCache() {
        return Caffeine.from(cacheSpec).build();
    }
}