package com.lty.model.dto.api;

import lombok.Data;

/**
 * 接口信息
 */
@Data
public class ApiInfo {

    /**
     * 接口路径
     */
    public String path;

    /**
     * 请求方式
     */
    public String method;

    /**
     * 接口名称
     */
    public String name;

    /**
     * 接口描述
     */
    public String description;
}
