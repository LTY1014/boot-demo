package com.lty.service;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Guava RateLimiter 单机令牌桶限流单元测试
 * SpringBoot3 + JUnit5
 */
class RateLimiterTest {

    /**
     * 测试1：acquire 阻塞获取令牌，平滑突发限流 SmoothBursty
     */
    @Test
    void testAcquireBlock() {
        // 每秒2个令牌 QPS=2
        RateLimiter rateLimiter = RateLimiter.create(2.0);

        for (int i = 1; i <= 3; i++) {
            double waitSeconds = rateLimiter.acquire();
            System.out.printf("testAcquireBlock | 第%d请求，等待时间: %.3fs%n", i, waitSeconds);
            assertTrue(waitSeconds >= 0);
        }
    }

    /**
     * 测试2：tryAcquire 非阻塞，立即返回 true/false（推荐接口限流场景）
     */
    @Test
    void testTryAcquireNoBlock() {
        RateLimiter rateLimiter = RateLimiter.create(2.0);

        // 前2个可以拿到
        boolean r1 = rateLimiter.tryAcquire();
        boolean r2 = rateLimiter.tryAcquire();
        assertTrue(r1);
        assertTrue(r2);

        // 第3个直接拿不到，不等待
        boolean r3 = rateLimiter.tryAcquire();
        assertFalse(r3);
    }

    /**
     * 测试3：tryAcquire 带超时时间，最多等待指定时长
     */
    @Test
    void testTryAcquireWithTimeout() {
        RateLimiter rateLimiter = RateLimiter.create(1.0);

        boolean success = rateLimiter.tryAcquire(1, 200, TimeUnit.MILLISECONDS);
        assertTrue(success);

        // 马上再尝试，只等200ms，拿不到返回false
        boolean fail = rateLimiter.tryAcquire(1, 200, TimeUnit.MILLISECONDS);
        assertFalse(fail);
    }

    /**
     * 测试4：一次获取多个令牌，权重限流
     */
    @Test
    void testAcquireMultiPermits() {
        // QPS=10
        RateLimiter rateLimiter = RateLimiter.create(10.0);
        // 当前请求一次性消耗3个许可
        double wait = rateLimiter.acquire(3);
        System.out.printf("testAcquireMultiPermits | 等待时间: %.3fs%n", wait);
        assertTrue(wait >= 0);
    }

    /**
     * 测试5：预热限流 SmoothWarmingUp，冷启动缓慢提升QPS
     */
    @Test
    void testWarmUpRateLimiter() {
        // QPS=5，预热3秒
        RateLimiter rateLimiter = RateLimiter.create(5.0, 3, TimeUnit.SECONDS);

        for (int i = 1; i <= 4; i++) {
            double wait = rateLimiter.acquire();
            System.out.printf("testWarmUpRateLimiter | 请求%d，等待时间 %.3fs%n", i, wait);
            assertTrue(wait >= 0);
        }
    }

    /**
     * 测试6：运行时动态修改限流器速率 setRate
     */
    @Test
    void testModifyRate() {
        RateLimiter rateLimiter = RateLimiter.create(2.0);
        assertEquals(2.0, rateLimiter.getRate());

        // 修改为每秒5个
        rateLimiter.setRate(5.0);
        assertEquals(5.0, rateLimiter.getRate());

        boolean ok = rateLimiter.tryAcquire();
        assertTrue(ok);
    }

    /**
     * 测试7：多线程并发测试 RateLimiter线程安全
     */
    @Test
    void testConcurrentRateLimit() throws InterruptedException {
        RateLimiter rateLimiter = RateLimiter.create(5.0);
        Runnable task = () -> {
            boolean acquire = rateLimiter.tryAcquire();
            System.out.printf("线程[%s] 获取令牌：%b%n", Thread.currentThread().getName(), acquire);
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }
}
