package com.lty.event.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WebhookManager 负责监听事件并调用所有 WebhookSender 实现类进行同步
 * 统一调度中心
 */
@Component
@RequiredArgsConstructor
public class WebhookManager {

    // 自动注入所有 Webhook 实现类
    private final List<WebhookSender> webhookSenders;

    // 监听事件 → 异步同步所有系统
    @Async
    @EventListener(BizDataUpdatedEvent.class)
    public void handleEvent(BizDataUpdatedEvent event) {
        for (WebhookSender sender : webhookSenders) {
            if (sender.isEnabled()) {
                sender.send(event);
            }
        }
    }
}