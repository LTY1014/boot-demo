package com.lty.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author lty
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;
}