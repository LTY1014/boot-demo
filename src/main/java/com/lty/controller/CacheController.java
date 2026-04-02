package com.lty.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.lty.common.BaseResponse;
import com.lty.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cache")
public class CacheController {

    @Resource
    private Cache<String, String> localUrlCache;

    // 统计方法
    @PostMapping("/stats")
    public BaseResponse<Map<String, String>> postCacheStats() {
        return ResultUtils.success(Map.of(
                "hitRate", String.format("%.2f", localUrlCache.stats().hitRate() * 100) + "%", // 缓存命中率
                "requestCount", String.valueOf(localUrlCache.stats().requestCount()), // 总请求数
                "hitCount", String.valueOf(localUrlCache.stats().hitCount()),  // 命中数
                "missCount", String.valueOf(localUrlCache.stats().missCount()), // 未命中数
                "estimatedSize", String.valueOf(localUrlCache.estimatedSize()) // 当前大小
        ));
    }

    // 放入键
    @PostMapping("/put")
    public BaseResponse<String> putCache(String key, String value) {
        localUrlCache.put(key, value);
        return ResultUtils.success("已放入缓存键: " + key);
    }

    // 获取键
    @PostMapping("/getBatch")
    public BaseResponse<Map<String, String>> getCaches(@RequestBody List<String> keys) {
        Map<String, String> res = new HashMap<>();
        keys.forEach(key -> {
            String value = localUrlCache.getIfPresent(key);
            if (value != null) {
                res.put(key, value);
            }
        });
        return ResultUtils.success(res);
    }

    // 3. 检查键是否存在
    @PostMapping("/containsKey")
    public BaseResponse<Boolean> containsKey(String key) {
        return ResultUtils.success(localUrlCache.asMap().containsKey(key));
    }

    // 清除键方法
    @PostMapping("/invalidate")
    public BaseResponse<String> invalidateCache(String key) {
        localUrlCache.invalidate(key);
        return ResultUtils.success("已清除缓存键: " + key);
    }

    // 清除所有方法
    @PostMapping("/invalidateAll")
    public BaseResponse<String> invalidateAllCache() {
        localUrlCache.invalidateAll();
        return ResultUtils.success("已清除所有缓存");
    }
}
