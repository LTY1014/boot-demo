package com.lty.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    /**
     * 生成验证码并返回图片
     */
    @GetMapping("/generate")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 创建验证码（宽120，高40，字符数为5，干扰线50条）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 50);
        // 将验证码值存储到 Session 中
        request.getSession().setAttribute("captcha", captcha.getCode());
        // 设置响应类型为图片
        response.setContentType("image/png");
        // 输出验证码图片
        captcha.write(response.getOutputStream());
    }

    /**
     * 验证验证码
     */
    @GetMapping("/verify")
    public String verifyCaptcha(HttpServletRequest request, String code) {
        // 获取 Session 中存储的验证码
        String sessionCaptcha = (String) request.getSession().getAttribute("captcha");

        if (sessionCaptcha != null && sessionCaptcha.equalsIgnoreCase(code)) {
            return "验证成功";
        } else {
            return "验证失败";
        }
    }
}
