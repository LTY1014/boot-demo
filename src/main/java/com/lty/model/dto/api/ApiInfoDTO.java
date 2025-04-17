package com.lty.model.dto.api;

import lombok.Data;

/**
 * 接口信息DTO
 */
@Data
public class ApiInfoDTO {

    /**
     * 接口地址
     */
    public String apiUrl;

    /**
     * 接口前缀
     */
    public String baseUrl;
}
