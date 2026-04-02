package com.lty.event.webhook.impl;

import com.lty.event.webhook.BizDataUpdatedEvent;
import com.lty.event.webhook.WebhookSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * B 系统 webhook 发送者
 */
@Component
@Slf4j
public class BSystemWebhookSender implements WebhookSender {

    @Override
    public String getSystemCode() {
        return "B_SYSTEM";
    }

    @Override
    public boolean isEnabled() {
        // 可写死/配置中心/数据库
        return true;
    }

    @Override
    public void send(BizDataUpdatedEvent event) {
        // B 系统自定义参数、签名、请求
        log.info("【B系统同步】dataId={}, content={}", event.getDataId(), event.getContent());
        // 实际发送 http 请求...
    }
}