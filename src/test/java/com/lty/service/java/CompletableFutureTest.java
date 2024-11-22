package com.lty.service.java;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture 测试类
 *
 * @author lty
 */
public class CompletableFutureTest {

    @Test
    public void taskTest() {
        // 异步任务
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 模拟耗时操作
            try {
                Thread.sleep(3000);
                System.out.println("async task");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "async task result";
        });
        // 获取结果
        future.thenAccept(result -> System.out.println("结果: " + result));
        //future.join();  // 这会阻塞，直到future完成
        System.out.println("main task");
    }

    // 使用 thenCombine 合并结果
    @Test
    public void CombineTest() {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            return 10;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            return 20;
        });

        CompletableFuture<Integer> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
            return result1 + result2;
        });

        combinedFuture.thenAccept(result -> System.out.println("合并结果: " + result));
    }

    //使用 thenCompose 进行依赖执行
    @Test
    public void thenComposeTest() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            return 10;
        });

        CompletableFuture<Integer> finalFuture = future.thenCompose(result -> {
            return CompletableFuture.supplyAsync(() -> result * 2);
        });

        finalFuture.thenAccept(result -> System.out.println("最终结果: " + result));
    }

    // 异常处理
    // exceptionally 处理异常
    @Test
    public void exceptionallyTest() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("发生异常");
            return 10;
        });

        future.exceptionally(ex -> {
            System.out.println("异常: " + ex.getMessage());
            return 0; // 返回默认值
        }).thenAccept(result -> System.out.println("结果: " + result));
    }
}
