package com.lty.util;

import com.lty.common.BaseResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * HttpClient 工具类
 */
public class HttpClientUtil {

    /**
     * 发起 GET 请求并解析响应
     */
    public static BaseResponse getRequest(String url) throws IOException {
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 GET 请求对象
            HttpGet httpGet = new HttpGet(url);
            // 执行请求并解析响应
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new IOException("Request failed with status code: " + response.getStatusLine().getStatusCode());
                }
                // (可加)对响应码进行二次验证
                String responseBody = EntityUtils.toString(response.getEntity());
                // 解析响应(这里响应类型为BaseResponse)
                BaseResponse baseResponse = GsonUtil.jsonToBean(responseBody, BaseResponse.class);
                return baseResponse;
            }
        }
    }

    public static BaseResponse postRequest(String url, Object requestBody) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 GET 请求对象
            HttpPost httpPost = new HttpPost(url);
            String json = GsonUtil.beanToJson(requestBody);
            // 添加 Header
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON.withCharset("UTF-8"));
            httpPost.setEntity(entity);

            // 执行请求并解析响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new IOException("Request failed with status code: " + response.getStatusLine().getStatusCode());
                }
                // (可加)对响应码进行二次验证
                String responseBody = EntityUtils.toString(response.getEntity());
                // 解析响应(这里响应类型为BaseResponse)
                BaseResponse baseResponse = GsonUtil.jsonToBean(responseBody, BaseResponse.class);
                return baseResponse;
            }
        }
    }
}
