package com.lty.event.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 事件定义（发布 / 订阅用）
 */
@Data
@AllArgsConstructor
public class BizDataUpdatedEvent {

    /**
     * 业务数据ID
     */
    private Long dataId;

    /**
     * 业务数据内容
     */
    private String content;

    /**
     * 业务数据状态
     */
    private Integer status;
}