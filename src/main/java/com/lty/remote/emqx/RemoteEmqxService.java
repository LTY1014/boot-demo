package com.lty.remote.emqx;

import cn.hutool.json.JSONUtil;
import com.lty.common.ErrorCode;
import com.lty.exception.BusinessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 对接EMQX API的服务类
 */
@Service
public class RemoteEmqxService {

    @Resource
    private EmqxConfig emqxConfig;

    @Resource
    private RestTemplate restTemplate;

    private static final String TOKEN_HEADER_PREFIX = "Bearer ";
    private static final String LOGIN_PATH = "/login";
    private static final String CLIENTS_PATH = "/clients";

    /**
     * 获取EMQX登录token
     *
     * @return 认证token
     * @throws BusinessException 当登录失败时抛出业务异常
     */
    public String fetchLogin() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", emqxConfig.getUsername());
        requestBody.put("password", emqxConfig.getPassword());

        ResponseEntity<Map> response = executePostRequest(emqxConfig.getUrl() + LOGIN_PATH, requestBody, buildCommonHeaders());
        return (String) response.getBody().get("token");
    }

    /**
     * 获取EMQX客户端列表
     *
     * @param page  页码
     * @param limit 每页数量
     * @return 客户端列表JSON字符串
     * @throws BusinessException 当请求失败时抛出业务异常
     */
    public String fetchClients(int page, int limit) {
        String token = fetchLogin();
        HttpHeaders headers = buildCommonHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, TOKEN_HEADER_PREFIX + token);

        String requestUrl = emqxConfig.getUrl() + CLIENTS_PATH + "?page=" + page + "&limit=" + limit;
        ResponseEntity<Map> response = executeGetRequest(requestUrl, headers);
        return JSONUtil.toJsonStr(response.getBody());
    }

    /**
     * 构建通用的HTTP请求头
     *
     * @return 包含通用设置的HttpHeaders
     */
    private HttpHeaders buildCommonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * 执行GET请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 响应实体
     * @throws BusinessException 当请求失败时抛出业务异常
     */
    private ResponseEntity<Map> executeGetRequest(String url, HttpHeaders headers) {
        return executeRequest(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
    }

    /**
     * 执行POST请求
     *
     * @param url     请求URL
     * @param body    请求体
     * @param headers 请求头
     * @return 响应实体
     * @throws BusinessException 当请求失败时抛出业务异常
     */
    private ResponseEntity<Map> executePostRequest(String url, Object body, HttpHeaders headers) {
        return executeRequest(url, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
    }

    /**
     * 执行HTTP请求的通用方法
     *
     * @param url           请求URL
     * @param method        HTTP方法
     * @param requestEntity 请求实体
     * @param responseType  响应类型
     * @param <T>           响应类型泛型
     * @return 响应实体
     * @throws BusinessException 当请求失败时抛出业务异常
     */
    private <T> ResponseEntity<T> executeRequest(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, method, requestEntity, responseType);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口返回非成功状态码: " + response.getStatusCode());
            }
            return response;
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口对接失败: " + e.getMessage());
        }
    }
}