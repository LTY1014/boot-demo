package com.lty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步线程池配置
 * @author lty
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    public Integer core_size=10;
    public Integer thread_max=30;
    public Integer queue_max=2000;

    @Override
    public Executor getAsyncExecutor() {
        //定义线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(core_size);
        //线程池最大线程数
        taskExecutor.setMaxPoolSize(thread_max);
        //线程队列最大线程数
        taskExecutor.setQueueCapacity(queue_max);
        //初始化
        taskExecutor.initialize();
        return taskExecutor;
    }
}
