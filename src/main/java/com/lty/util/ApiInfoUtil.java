package com.lty.util;

import com.lty.model.dto.api.ApiInfo;
import com.lty.model.dto.api.ApiInfoDTO;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiInfoUtil {

    /**
     * 获取接口信息列表
     */
    public static List<ApiInfo> getApiInfoList(ApiInfoDTO dto) throws Exception {
        List<ApiInfo> apiInfoList = new ArrayList<>();
        String apiUrl = dto.getApiUrl();
        String baseUrl = dto.getBaseUrl();
        // 解析远程 OpenAPI 文档
        OpenApi3 api = new OpenApi3Parser().parse(new URL(apiUrl), false);
        api.getPaths().forEach((path, pathItem) -> {
            // 去除前缀/api
            String apiPath = path.replace(baseUrl, "");
            pathItem.getOperations().forEach((httpMethod, operation) -> {
                ApiInfo apiInfo = new ApiInfo();
                apiInfo.setPath(apiPath);
                apiInfo.setMethod(httpMethod);
                apiInfo.setName(operation.getOperationId());
                apiInfo.setDescription(operation.getSummary());
                apiInfoList.add(apiInfo);
            });
        });
        return apiInfoList;
    }
}
