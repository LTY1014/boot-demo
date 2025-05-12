package com.lty.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.lty.model.dto.Person;
import com.lty.util.ServletUtil;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Hutool测试工具
 *
 * @author lty
 */
public class HuToolTest {

    @Test
    public void testSystemUtil() {
        System.out.println(SystemUtil.getJvmSpecInfo().getVersion());
        System.out.println(SystemUtil.getOsInfo());
        System.out.println(SystemUtil.getUserInfo());
        System.out.println(SystemUtil.getHostInfo());
        System.out.println(SystemUtil.getRuntimeInfo());
    }

    @Test
    public void testJSON() {
        List<Person> personList = Person.getPersonList();
        String json = JSONUtil.toJsonStr(personList);
        System.out.println(json);

        JSONArray jsonArray = JSONUtil.parseArray(json);
        System.out.println(jsonArray);

        for (Object obj : jsonArray) {
            JSONObject jsonObject = JSONUtil.parseObj(obj);
            System.out.println(jsonObject.getStr("name"));
            if (jsonObject.containsKey("age")) {
                System.out.println(jsonObject.getStr("age"));
            }
        }
    }

    @Test
    public void testUrl() {
        String imageUrl = "http://139.224.186.190:8095/api/file/2024/10/27/91a923deedfa4a7faedf4ad0550dd158.png";
        String fileName = "downloaded-image.png"; // 下载文件名

        // 使用Hutool的HttpRequest来发送HTTP GET请求
        HttpResponse urlResponse = HttpRequest.get(imageUrl).execute();

        HttpServletResponse response = ServletUtil.getResponse();
        // 检查请求是否成功
        if (urlResponse.isOk()) {
            try (
                    InputStream inputStream = urlResponse.bodyStream(); // 获取响应体输入流
                    OutputStream outputStream = response.getOutputStream() // 获取响应输出流
            ) {
                // 设置响应头
                response.setContentType("application/octet-stream");
                response.setCharacterEncoding("UTF-8");
                String encodedFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);

                // 将输入流数据写入输出流
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush(); // 强制将数据写入响应
            } catch (IOException e) {
                throw new RuntimeException("文件下载失败", e);
            }
        } else {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 设置错误状态码
                response.getWriter().write("文件下载失败，HTTP响应码：" + urlResponse.getStatus());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testHttpRequest() {
        while (true){
            try {
                HttpRequest request = HttpRequest.post("https://api.uomg.com/api/rand.qinghua")
                        .form("format", "json")
                        .timeout(5000);

                HttpResponse response = request.execute();
                if (response.isOk()) {
                    System.out.println(response.body());
                }
                Thread.sleep(2000); // 每次请求间隔 3 秒
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
