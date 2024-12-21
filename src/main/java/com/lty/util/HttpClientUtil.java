package com.lty.util;

import com.lty.common.BaseResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * HttpClient 工具类
 */
public class HttpClientUtil {

    // 创建一个共享的 CookieStore 和 Context
    private static final BasicCookieStore COOKIE_STORE = new BasicCookieStore();
    private static final HttpClientContext CONTEXT = HttpClientContext.create();

    static {
        CONTEXT.setCookieStore(COOKIE_STORE);
    }

    /**
     * 发起 GET 请求并解析响应
     */
    public static BaseResponse getRequest(String url) throws IOException {
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = createHttpClient()) {
            // 创建 GET 请求对象
            HttpGet httpGet = new HttpGet(url);
            setDefaultHeaders(httpGet);
            // 执行请求并解析响应
            try (CloseableHttpResponse response = httpClient.execute(httpGet, CONTEXT)) {
                validateResponse(response);
                String responseBody = EntityUtils.toString(response.getEntity());
                // 解析响应(这里响应类型为BaseResponse)
                BaseResponse baseResponse = GsonUtil.jsonToBean(responseBody, BaseResponse.class);
                return baseResponse;
            }
        }
    }

    public static BaseResponse postRequest(String url, Object requestBody) throws IOException {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpPost httpPost = new HttpPost(url);
            setDefaultHeaders(httpPost);

            if (requestBody != null) {
                String json = GsonUtil.beanToJson(requestBody);
                StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON.withCharset("UTF-8"));
                httpPost.setEntity(entity);
            }

            // 执行请求并解析响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost, CONTEXT)) {
                validateResponse(response);
                String responseBody = EntityUtils.toString(response.getEntity());
                // 解析响应(这里响应类型为BaseResponse)
                BaseResponse baseResponse = GsonUtil.jsonToBean(responseBody, BaseResponse.class);
                return baseResponse;
            }
        }
    }

    /**
     * 设置默认请求头
     */
    private static void setDefaultHeaders(HttpRequestBase request) {
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
    }


    /**
     * 校验 HTTP 响应状态
     */
    private static void validateResponse(CloseableHttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException("Request failed with status code: " + statusCode);
        }
        // (可加)对响应码进行二次验证
    }


    /**
     * 创建 HttpClient 实例，支持超时设置
     */
    private static CloseableHttpClient createHttpClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000) // 连接超时
                .setSocketTimeout(5000) // 读取超时
                .build();

        //** 请求头设置 **/
        return HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setDefaultCookieStore(COOKIE_STORE)
                .build();
    }

    /**
     * 添加 Cookie
     */
    public static void addCookie(String name, String value, String domain) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath("/");
        COOKIE_STORE.addCookie(cookie);
    }

    /**
     * 清除所有 Cookies
     */
    public static void clearCookies() {
        COOKIE_STORE.clear();
    }
}
