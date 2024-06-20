package com.lty.controller;

import com.lty.annotation.AuthCheck;
import com.lty.common.BaseResponse;
import com.lty.common.ErrorCode;
import com.lty.common.ResultUtils;
import com.lty.exception.BusinessException;
import com.lty.service.UserService;
import com.lty.util.IpInfoUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
}
