package com.lty.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 高性能系统缓存类
 */
public class SysCacheUtil {

    // 单例实例
    private static class Holder {
        private static final SysCacheUtil INSTANCE = new SysCacheUtil();
    }

    public static SysCacheUtil getInstance() {
        return Holder.INSTANCE;
    }

    // 缓存存储结构
    private final ConcurrentHashMap<String, CacheEntry> cacheMap = new ConcurrentHashMap<>();
    // 定时任务执行器（守护线程）
    private final ScheduledExecutorService scheduledExecutorService;
    // 批量操作锁
    private final ReentrantLock batchOperationLock = new ReentrantLock();

    // 私有构造方法
    private SysCacheUtil() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sys-cache-cleaner");
            t.setDaemon(true);
            return t;
        });

        // 初始延迟10秒，每分钟清理一次
        scheduledExecutorService.scheduleAtFixedRate(this::cleanUp, 10, 60, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // ======================================
    // ========== 核心操作方法 ==============
    // ======================================

    /**
     * 存入缓存（永不过期）
     */
    public void set(String key, Object value) {
        cacheMap.put(key, new CacheEntry(value, -1));
    }

    /**
     * 存入缓存（带过期时间）
     */
    public void set(String key, Object value, long duration, TimeUnit unit) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        long expireTime = System.currentTimeMillis() + unit.toMillis(duration);
        cacheMap.put(key, new CacheEntry(value, expireTime));
    }

    /**
     * 获取缓存值（自动处理过期）
     */
    public Object get(String key) {
        CacheEntry entry = cacheMap.get(key);
        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            cacheMap.remove(key);
            return null;
        }

        return entry.getValue();
    }

    /**
     * 获取缓存值（带默认值）
     */
    public Object get(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 懒加载模式获取缓存值
     */
    public Object get(String key, Supplier<Object> valueLoader, long duration, TimeUnit unit) {
        Object value = get(key);
        if (value != null) {
            return value;
        }

        batchOperationLock.lock();
        try {
            value = get(key); // 双重检查
            if (value != null) {
                return value;
            }

            value = valueLoader.get();
            if (value != null) {
                set(key, value, duration, unit);
            }
            return value;
        } finally {
            batchOperationLock.unlock();
        }
    }

    // ======================================
    // ========== 辅助操作方法 ==============
    // ======================================

    /**
     * 删除单个缓存
     */
    public void remove(String key) {
        cacheMap.remove(key);
    }

    /**
     * 批量删除缓存
     */
    public void removeAll(Iterable<String> keys) {
        batchOperationLock.lock();
        try {
            for (String key : keys) {
                cacheMap.remove(key);
            }
        } finally {
            batchOperationLock.unlock();
        }
    }

    /**
     * 清空所有缓存
     */
    public void clear() {
        batchOperationLock.lock();
        try {
            cacheMap.clear();
        } finally {
            batchOperationLock.unlock();
        }
    }

    /**
     * 检查缓存是否存在
     */
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    /**
     * 获取缓存大小
     */
    public int size() {
        return cacheMap.size();
    }

    // ======================================
    // ========== 内部实现方法 ==============
    // ======================================

    /**
     * 定时清理过期条目（分批处理）
     */
    private void cleanUp() {
        int batchSize = 1000;
        int removedCount = 0;

        Iterator<Map.Entry<String, CacheEntry>> iterator = cacheMap.entrySet().iterator();
        long currentTime = System.currentTimeMillis();

        while (iterator.hasNext() && removedCount < batchSize) {
            Map.Entry<String, CacheEntry> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                removedCount++;
            }
        }

        // 超过阈值时触发GC
        if (removedCount > batchSize / 2) {
            System.gc();
        }
    }

    /**
     * 优雅关闭线程池
     */
    private void shutdown() {
        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ======================================
    // ========== 内部数据结构 ==============
    // ======================================

    /**
     * 缓存条目
     */
    private static class CacheEntry {
        private final Object value;
        private final long expireTime;

        public CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        public boolean isExpired() {
            return expireTime > 0 && System.currentTimeMillis() > expireTime;
        }

        public Object getValue() {
            return value;
        }
    }

    // ======================================
    // ========== 测试用例 ================
    // ======================================

    public static void main(String[] args) throws InterruptedException {
        SysCacheUtil cache = SysCacheUtil.getInstance();

        // 基础功能测试
        cache.set("name", "Alice", 3, TimeUnit.SECONDS);
        System.out.println("name: " + cache.get("name")); // 输出: Alice

        // 懒加载测试
        String key = "data";
        Object data = cache.get(key, () -> {
            System.out.println("Loading data from source...");
            return "cached-data";
        }, 5, TimeUnit.SECONDS);
        System.out.println("data: " + data); // 输出: cached-data

        // 过期测试
        Thread.sleep(4000);
        System.out.println("name after expiration: " + cache.get("name")); // 输出: null
        System.out.println("data after expiration: " + cache.get("data")); // 输出: cached-data
    }
}