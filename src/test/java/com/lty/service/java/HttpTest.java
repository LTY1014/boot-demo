package com.lty.service.java;

import com.lty.model.dto.user.UserLoginRequest;
import com.lty.util.GsonUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * HttpClient 测试类
 */
public class HttpTest {

    @Test
    public void test() {
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 调用 GET 请求示例
            executeGetRequest(httpClient);
            // 调用 POST 请求示例
            executePostRequest(httpClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 执行 GET 请求
    private static void executeGetRequest(CloseableHttpClient httpClient) throws IOException {
        // 创建 GET 请求对象
        HttpGet httpGet = new HttpGet("http://139.224.186.190:8088/api/");

        System.out.println("Executing GET request: " + httpGet.getMethod() + " " + httpGet.getURI());

        // 执行 GET 请求并处理响应
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            System.out.println("GET Response Status: " + response.getStatusLine().getStatusCode());
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("GET Response Body: " + responseBody);
        }
    }

    // 执行 POST 请求
    private static void executePostRequest(CloseableHttpClient httpClient) throws IOException {
        // 创建 POST 请求对象
        HttpPost httpPost = new HttpPost("http://139.224.186.190:8088/api/user/login");

        // 构造请求体
        //String json = "{\n" +
        //        "  \"userAccount\": \"test\",\n" +
        //        "  \"userPassword\": \"123456\"\n" +
        //        "}";

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUserAccount("test");
        userLoginRequest.setUserPassword("123456");
        String json = GsonUtil.beanToJson(userLoginRequest);

        System.out.println(json);
        // 添加 Header
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON.withCharset("UTF-8"));
        httpPost.setEntity(entity);

        System.out.println("Executing POST request: " + httpPost.getMethod() + " " + httpPost.getURI());

        // 执行 POST 请求并处理响应
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            System.out.println("POST Response Status: " + response.getStatusLine().getStatusCode());
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("POST Response Body: " + responseBody);
        }
    }

}
