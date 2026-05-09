package com.lty.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarker 测试控制器
 * 注意：这里必须用 @Controller，不能用 @RestController
 */
@Controller
public class FreeMarkerController {

    /**
     * 访问路径：http://localhost:8080/test-view
     * Model：用于向后端模板传递数据
     */
    @GetMapping("/test-view")
    public String testFreeMarker(Model model) {
        // 1. 传递普通字符串
        model.addAttribute("title", "Spring Boot 整合 FreeMarker 成功！");
        model.addAttribute("name", "FreeMarker 模板引擎");

        // 2. 传递对象/Map
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1001);
        user.put("username", "张三");
        user.put("age", 25);
        model.addAttribute("user", user);

        // 3. 传递集合
        model.addAttribute("hobbyList", Arrays.asList("编程", "篮球", "阅读"));

        // 4. 返回模板文件名（不需要写后缀，配置文件已指定 .ftlh）
        // 注意：不能与请求路径相同，否则会 Circular view path
        return "test";
    }
}