package com.lty.service;

import com.lty.model.dto.api.ApiInfo;
import com.lty.model.dto.api.ApiInfoDTO;
import com.lty.util.ApiInfoUtil;
import com.lty.util.ServletUtil;
import org.junit.Test;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.List;

public class ApiPathTest {
    public static void main(String[] args) {
        try {
            HttpServletRequest request = ServletUtil.getRequest();
            String apiUrl = "http://localhost:8088/api/v3/api-docs";
            //String baseUrl = request.getContextPath();
            String baseUrl = "/api";

            // 解析远程 OpenAPI 文档
            OpenApi3 api = new OpenApi3Parser().parse(new URL(apiUrl), false);

            // 逐层提取路由信息
            api.getPaths().forEach((path, pathItem) -> {
                System.out.println("接口路径: " + path);
                // 去除前缀/api
                path = path.replace(baseUrl, "");
                System.out.println(" |-- 路径: " + path);
                
                // 获取路径下的操作方法（Operation）
                pathItem.getOperations().forEach((httpMethod, operation) -> {
                    System.out.println(" |-- 接口方式: " + httpMethod);
                    System.out.println(" |-- 接口名: " + operation.getOperationId());
                    System.out.println(" |-- 接口描述: " + operation.getSummary());
                });
            });
            
        } catch (Exception e) {
            System.err.println("解析错误：" + e.getMessage());
        }
    }

    @Test
    public void test() {
        ApiInfoDTO dto = new ApiInfoDTO();
        dto.setApiUrl("http://localhost:8088/api/v3/api-docs");
        dto.setBaseUrl("/api");
        try {
            List<ApiInfo> apiInfoList = ApiInfoUtil.getApiInfoList(dto);
            System.out.println("接口信息列表:"+ apiInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}