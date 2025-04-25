package com.lty.config;

import com.lty.interceptor.PathInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * 全局跨域配置
 *
 * @author lty
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Resource
    private PathInterceptor demoInterceptor;

    /** 配置跨域 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送Cookie
                .allowCredentials(true)
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 设置允许的方法
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //定义要拦截的路径
        String[] addPathPatterns = {
                "/**"
        };

        //排除不需要拦截的路径
        String[] excludePathPatterns = {
                "/doc.html",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/v2/api-docs",
                "/webjars/**"
        };

        List<HandlerInterceptor> interceptors = List.of(demoInterceptor);
        for (HandlerInterceptor interceptor : interceptors) {
            // 添加拦截器对象
            registry.addInterceptor(interceptor)
                    .addPathPatterns(addPathPatterns)
                    .excludePathPatterns(excludePathPatterns);
        }
    }
}
