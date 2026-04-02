package com.lty.event.webhook.impl;

import com.lty.event.webhook.BizDataUpdatedEvent;
import com.lty.event.webhook.WebhookSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * C系统的Webhook发送器实现
 */
@Component
@Slf4j
public class CSystemWebhookSender implements WebhookSender {

    @Override
    public String getSystemCode() {
        return "C_SYSTEM";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void send(BizDataUpdatedEvent event) {
        log.info("【C系统同步】dataId={}, status={}", event.getDataId(), event.getStatus());
    }
}