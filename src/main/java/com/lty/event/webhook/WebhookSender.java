package com.lty.event.webhook;

/**
 * 统一 Webhook 接口
 */
public interface WebhookSender {

    // 系统唯一标识
    String getSystemCode();

    // 是否开启同步
    boolean isEnabled();

    // 执行同步
    void send(BizDataUpdatedEvent event);
}