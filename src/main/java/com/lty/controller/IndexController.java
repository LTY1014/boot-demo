package com.lty.controller;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.lty.annotation.AuthCheck;
import com.lty.common.BaseResponse;
import com.lty.common.ErrorCode;
import com.lty.common.ResultUtils;
import com.lty.exception.BusinessException;
import com.lty.service.BasicCustomerService;
import com.lty.service.UserService;
import com.lty.util.BusinessFileUtil;
import com.lty.util.CommandUtil;
import com.lty.util.IpInfoUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @Resource
    private BasicCustomerService basicCustomerService;


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

    @ApiOperation(value = "获取设备信息")
    @GetMapping("/device")
    public BaseResponse<String> device(HttpServletRequest request) {
        // 利用Hutool的UserAgentUtil来解析User-Agent
        UserAgent ua = UserAgentUtil.parse(request.getHeader("user-agent"));
        String device = ua.getBrowser().toString() + " " + ua.getVersion() + " | " + ua.getPlatform().toString()
                + " " + ua.getOs().toString() + " | " +  (ua.isMobile() ? "移动端" : "PC端");
        return ResultUtils.success(device);
    }

    /**
     * 示例1：执行Shell脚本（Linux/Mac）
     */
    @PostMapping("/execute-shell")
    public String executeShellScript() {
        try {
            // 执行 /opt/test.sh 脚本（需替换为你的脚本路径）
            List<String> command = Arrays.asList("/bin/bash", "/opt/test.sh");
            String result = CommandUtil.executeCommand(command);
            return "脚本执行成功：\n" + result;
        } catch (Exception e) {
            return "脚本执行失败：" + e.getMessage();
        }
    }

    /**
     * 示例2：执行简单系统命令（如查看Linux系统信息）
     */
    @PostMapping("/execute-system")
    public String executeSystemCommand() {
        try {
            // 执行 ls -l 命令（查看当前目录文件）
            List<String> command = Arrays.asList("ls", "-l");
            String result = CommandUtil.executeCommand(command);
            return "命令执行成功：\n" + result;
        } catch (Exception e) {
            return "命令执行失败：" + e.getMessage();
        }
    }

    /**
     * 示例3：执行Windows批处理脚本（Windows系统）
     */
    @PostMapping("/execute-bat")
    public String executeBatScript() {
        try {
            // 执行 D:\test.bat 脚本
            List<String> command = Arrays.asList("cmd", "/c", "D:\\test.bat");
            String result = CommandUtil.executeCommand(command);
            return "批处理执行成功：\n" + result;
        } catch (Exception e) {
            return "批处理执行失败：" + e.getMessage();
        }
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public BaseResponse<Map<String, String>> reloadFactoryConfig(@RequestPart(value = "file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        }
        File jsonFile = new File(BasicCustomerService.JSON_FILE);
        BusinessFileUtil.saveMultipartFileToLocal(file, jsonFile);
        return ResultUtils.success(basicCustomerService.reloadFactoryConfig());
    }
}
