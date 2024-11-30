package com.lty.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.lty.annotation.AuthCheck;
import com.lty.common.BaseResponse;
import com.lty.common.ErrorCode;
import com.lty.common.ResultUtils;
import com.lty.exception.BusinessException;
import com.lty.service.UserService;
import com.lty.util.IpInfoUtil;
import com.lty.util.ServletUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lty
 */
@Slf4j
@RestController
public class IndexController {
    @Value("${server.port}")
    Integer port;

    @Resource
    private UserService userService;


    @ApiOperation(value = "默认")
    @GetMapping(value = "/")
    public BaseResponse<String> index() {
        log.info("index() called with parameters => ");
        return ResultUtils.success("success");
    }

    @ApiOperation(value = "失败", response = ResultUtils.class)
    @GetMapping("/fail")
    public BaseResponse error() {
        log.info("error() called with parameters => ");
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数失败");
    }

    @ApiOperation(value = "参数请求")
    @GetMapping("/hello")
    public BaseResponse<String> hello(@RequestParam(value = "name", required = false) String name) {
        String str = "hello," + name;
        log.info("hello() called with parameters => [name = {}]", name);
        return ResultUtils.success(str);
    }

    @AuthCheck(anyRole = {"admin", "test", "user"})
    @ApiOperation(value = "资源页")
    @GetMapping("/resource")
    public BaseResponse<String> resource() {
        if (userService.getLoginUser().getId() < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success("资源页访问成功");
    }

    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "管理员资源页")
    @GetMapping("/adminResource")
    public BaseResponse<String> adminResource() {
        if (!userService.isAdmin()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success("管理员资源页访问成功");
    }

    @ApiOperation(value = "获取端口号")
    @GetMapping("/getPort")
    public BaseResponse<String> getPort() {
        String str = "当前端口为: " + port;
        return ResultUtils.success(str);
    }

    @ApiOperation(value = "获取IP")
    @GetMapping("/getIp")
    public BaseResponse<String> getIp(HttpServletRequest request) {
        return ResultUtils.success(IpInfoUtil.getIpAddress(request));
    }

    @ApiOperation(value = "获取ClientIP")
    @GetMapping("/getClientIp")
    public BaseResponse<String> getClientIp(String ip) {
        return ResultUtils.success(IpInfoUtil.getClient(ip));
    }

    @GetMapping("/download")
    public void download(String fileUrl) throws IOException {
        // 测试用例：http://127.0.0.1:8088/api/download?fileUrl=http://139.224.186.190:8095/api/file/2024/10/27/91a923deedfa4a7faedf4ad0550dd158.png

        // 文件URL得到最后一段字符串作为下载文件名
        String fileName = System.currentTimeMillis() + fileUrl.substring(fileUrl.lastIndexOf("."));

        // 使用Hutool的HttpRequest来发送HTTP GET请求
        HttpResponse urlResponse = HttpRequest.get(fileUrl).execute();

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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 设置错误状态码
            response.getWriter().write("文件下载失败，HTTP响应码：" + urlResponse.getStatus());
        }
    }
}
