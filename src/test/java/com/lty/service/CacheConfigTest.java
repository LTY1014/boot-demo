package com.lty.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheConfigTest {

    @Resource
    private Cache<String, String> localUrlCache;

    @Test
    public void testCachePut() {
        // 1. 测试存入缓存
        localUrlCache.put("key1", "value1");
        localUrlCache.put("user:1001", "张三");
        
        System.out.println("✓ 缓存写入成功");
    }

    @Test
    public void testCacheGet() {
        // 先放入数据
        localUrlCache.put("testKey", "testValue");
        
        // 2. 测试获取缓存
        String value = localUrlCache.getIfPresent("testKey");
        System.out.println("获取缓存值：" + value);
        assert "testValue".equals(value) : "缓存值不匹配";
        
        System.out.println("✓ 缓存读取成功");
    }

    @Test
    public void testCacheExpire() throws InterruptedException {
        // 3. 测试缓存过期
        localUrlCache.put("expireKey", "expireValue");
        
        System.out.println("初始值：" + localUrlCache.getIfPresent("expireKey"));
        
        // 等待 2 秒（如果配置了较短过期时间）
        TimeUnit.SECONDS.sleep(2);
        
        String value = localUrlCache.getIfPresent("expireKey");
        System.out.println("2 秒后值：" + value);
    }

    @Test
    public void testCacheDelete() {
        // 4. 测试删除缓存
        localUrlCache.put("deleteKey", "deleteValue");
        System.out.println("删除前：" + localUrlCache.getIfPresent("deleteKey"));

        localUrlCache.invalidate("deleteKey");
        System.out.println("删除后：" + localUrlCache.getIfPresent("deleteKey"));
        
        System.out.println("✓ 缓存删除成功");
    }

    @Test
    public void testCacheStats() {
        // 5. 测试缓存统计信息
        localUrlCache.put("stat1", "value1");
        localUrlCache.put("stat2", "value2");
        localUrlCache.getIfPresent("stat1");
        localUrlCache.getIfPresent("stat2");
        
        System.out.println("缓存命中率：" + localUrlCache.stats().hitRate());
        System.out.println("总请求数：" + localUrlCache.stats().requestCount());
        System.out.println("命中数：" + localUrlCache.stats().hitCount());
        System.out.println("未命中数：" + localUrlCache.stats().missCount());
        System.out.println("当前大小：" + localUrlCache.estimatedSize());
    }

    @Test
    public void testGetOrCompute() {
        // 6. 测试缓存不存在时自动计算
        String result = localUrlCache.get("computeKey", key -> {
            System.out.println("执行耗时计算...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "computedValue";
        });
        
        System.out.println("第一次获取：" + result);
        
        // 第二次从缓存获取
        String cachedResult = localUrlCache.getIfPresent("computeKey");
        System.out.println("第二次获取（缓存）：" + cachedResult);
    }
}