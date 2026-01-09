package com.lty.service;

import com.lty.util.bean.MailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Resource
    private MailUtil mailUtil;

    @Test
    public void test() {
        File file = new File("C:\\Users\\Administrator\\Desktop\\1.xlsx");
        List<File> files = new ArrayList<>();
        files.add(file);
        Boolean b = mailUtil.sendAttachmentMail(new String[]{"xxx@email.com"}, "主题", "内容", files);
        System.out.println(b);
    }

    @Test
    public void testSendHtmlMail(){
        String subject = "欢迎注册";
        String userEmail = "xxx@email.com";

        String welcomeHtml = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "  <h2 style='color: #2c3e50;'>欢迎加入我们！</h2>" +
                "  <p>亲爱的用户，</p>" +
                "  <p>感谢您注册我们的服务！</p>" +
                "  <p>您的账号已经激活，现在可以开始使用所有功能。</p>" +
                "  <div style='background-color: #ecf0f1; padding: 15px; margin: 20px 0; border-radius: 5px;'>" +
                "    <p><strong>登录信息：</strong></p>" +
                "    <p>邮箱: " + userEmail + "</p>" +
                "  </div>" +
                "  <p>如有任何问题，请随时联系我们。</p>" +
                "  <p>祝您使用愉快！</p>" +
                "</div>";
        mailUtil.sendHtmlMail(new String[]{userEmail}, subject, welcomeHtml);
    }
}