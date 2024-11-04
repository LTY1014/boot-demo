package com.lty.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.lty.common.BaseResponse;
import com.lty.model.dto.user.UserLoginRequest;
import com.lty.model.vo.UserVO;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * RestTemplate请求测试
 *
 * @author lty
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTemplateTest {

    private final Gson gson = new Gson();

    // GET请求
    @Test
    public void test() {
        String url = "http://139.224.186.190:8088/api";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // 检查响应状态码
        if (response.getStatusCode().is2xxSuccessful()) {
            // 返回响应体
            System.out.println(response.getBody());
            //return response.getBody();
        } else {
            // 处理错误情况
            throw new RuntimeException("Failed to fetch data: " + response.getStatusCode());
        }
    }

    // POST请求(json字符串)
    @Test
    public void test2() {
        String url = "http://139.224.186.190:8088/api/user/login"; // 替换为你的API URL
        RestTemplate restTemplate = new RestTemplate();

        // 创建请求头(如果需要)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<>();
        map.put("userAccount", "admin");
        map.put("userPassword", "123456");
        // 将Map转换为JSON字符串
        String jsonRequestBody = gson.toJson(map);
        System.out.println("Request JSON: " + jsonRequestBody);

        // 创建HttpEntity对象，它包含了请求头和请求体(JSON字符串)
        HttpEntity<String> request = new HttpEntity<>(jsonRequestBody, headers);
        // 发送POST请求并接收响应
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // 检查响应状态码
        if (response.getStatusCode().is2xxSuccessful()) {
            // 返回响应体(也是JSON字符串)
            System.out.println(response);
            Map<String, String> responseMap = gson.fromJson(response.getBody(), Map.class);
            // 相应数据
            if (responseMap.containsKey("data")) {
                // 因为泛型擦除，所以先转Object
                Object dataObj = responseMap.get("data");
                Map dataMap = gson.fromJson(gson.toJson(dataObj), Map.class);
                System.out.println(dataMap.get("userRole"));
            }
        } else {
            // 处理错误情况
            throw new RuntimeException("Failed to post JSON data: " + response.getStatusCode());
        }
    }

    // POST请求(json字符串)请求带上cookie
    @Test
    public void test3() {
        String loginUrl = "http://139.224.186.190:8088/api/user/login"; // 替换为你的API URL
        String resourceUrl = "http://139.224.186.190:8088/api/adminResource"; // 替换为你的API URL

        // 配置RestTemplate以处理cookies
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(new BasicCookieStore())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);  // 使用共享的RestTemplate实例

        // 登录请求
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("userAccount", "admin");
        loginMap.put("userPassword", "123456");

        String jsonLoginRequestBody = gson.toJson(loginMap);
        HttpEntity<String> loginRequest = new HttpEntity<>(jsonLoginRequestBody, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String.class);

        if (!loginResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to login: " + loginResponse.getStatusCode());
        }

        // 假设登录成功，现在访问资源
        HttpHeaders resourceHeaders = new HttpHeaders();
        // 通常，资源请求的headers可能不需要再次设置Content-Type，除非要发送JSON body

        // 假设发送GET请求获取资源
        ResponseEntity<String> resourceResponse = restTemplate.getForEntity(resourceUrl, String.class);

        if (resourceResponse.getStatusCode().is2xxSuccessful()) {
            // 处理资源响应
            System.out.println(resourceResponse);
            Map<String, Object> resourceResponseMap = gson.fromJson(resourceResponse.getBody(), new TypeToken<Map<String, Object>>() {
            }.getType());

            // 检查并处理资源数据
            if (resourceResponseMap.containsKey("data")) {
                Object data = resourceResponseMap.get("data");
                // 根据需要处理data
                // 注意：这里不需要再次将data转换为JSON字符串再解析为Map，除非data本身是一个JSON字符串
                // 如果data是一个复杂的对象，你可能需要定义一个Java类来接收它，而不是使用Map
                System.out.println(data);
            }
        } else {
            // 处理访问资源时的错误情况
            throw new RuntimeException("Failed to access resource: " + resourceResponse.getStatusCode());
        }
    }

    // POST对象请求
    @Test
    public void test4() {
        String url = "http://139.224.186.190:8088/api/user/login"; // 替换为你的API URL

        // 配置RestTemplate以处理cookies
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(new BasicCookieStore())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);  // 使用共享的RestTemplate实例


        // 登录请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UserLoginRequest requestBody = new UserLoginRequest();
        requestBody.setUserAccount("admin");
        requestBody.setUserPassword("1234567");

        HttpEntity<UserLoginRequest> request = new HttpEntity<>(requestBody, headers);
        // 发送POST请求并接收响应(接收对象要有无参构造函数)
        ResponseEntity<BaseResponse> response = restTemplate.postForEntity(url, request, BaseResponse.class);

        // 检查响应状态码
        if (response.getStatusCode().is2xxSuccessful()) {
            BaseResponse responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            UserVO userVO = objectMapper.convertValue(responseBody.getData(), UserVO.class);
            System.out.println(userVO);
        } else {
            // 处理错误情况
            throw new RuntimeException("Failed to fetch data: " + response.getStatusCode());
        }
    }
}