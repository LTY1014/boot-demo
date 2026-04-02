package com.lty.event.webhook;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 业务服务类，负责执行业务更新并发布事件
 */
@Service
public class BizDataService {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    public void updateData(Long dataId, String content, Integer status) {
        // 1. 执行本系统业务更新（数据库操作）
        System.out.println("业务数据更新完成：dataId=" + dataId);
        // 2. 发布事件（完全不关心同步给谁）
        eventPublisher.publishEvent(new BizDataUpdatedEvent(dataId, content, status));
    }
}