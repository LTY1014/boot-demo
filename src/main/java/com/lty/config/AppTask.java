package com.lty.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * AppTask(启动关闭时加载)
 * @author lty
 */
@Slf4j
@Component
public class AppTask {

    @PostConstruct
    public void init(){
        log.info("AppTask init");
    }

    @PreDestroy
    public void preDestroy(){
        log.info("AppTask preDestroy");
    }
}