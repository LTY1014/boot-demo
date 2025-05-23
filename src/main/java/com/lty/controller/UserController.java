package com.lty.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.lty.annotation.AuthCheck;
import com.lty.common.BaseResponse;
import com.lty.common.DeleteRequest;
import com.lty.common.ErrorCode;
import com.lty.common.ResultUtils;
import com.lty.constant.UserConstant;
import com.lty.exception.BusinessException;
import com.lty.model.dto.user.*;
import com.lty.model.entity.User;
import com.lty.model.vo.UserVO;
import com.lty.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lty
 */
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {
    @Resource
    public UserService userService;

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userLoginRequest);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @ApiOperation(value = "用户注销")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout() {
        boolean result = userService.userLogout();
        return ResultUtils.success(result);
    }

    @ApiOperation(value = "获取当前用户")
    @GetMapping("/current")
    public BaseResponse<UserVO> getLoginUser() {
        User user = userService.getLoginUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @ApiOperation(value = "用户修改")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        // 个人或管理员才可修改
        User loginUser = userService.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (!(loginUser.getId().equals(userUpdateRequest.getId()) || loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE))) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        boolean result = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(result);
    }

    @ApiOperation(value = "id获取")
    @GetMapping("/get")
    public BaseResponse<UserVO> getById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "id不存在");
        }
        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(user, userVo);
        return ResultUtils.success(userVo);
    }

    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "列表获取")
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest) {

        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> qw = userService.getQueryWrapper(userQueryRequest);
        List<User> userList = userService.list(qw);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    @ApiOperation(value = "分页获取")
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //分页基本字段
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 反爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "查询数量过多");
        }
        Page<User> page = new Page<>(current, size);
        Page<User> userPage = userService.page(page, userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    @ApiOperation(value = "用户删除")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //管理员可删除
        User loginUser = userService.getLoginUser();
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        long id = deleteRequest.getId();
        User oldUser = userService.getById(id);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @ApiOperation("修改密码")
    @PostMapping("/updatePassword")
    public BaseResponse<Boolean> updatePassword(@RequestBody UserPasswordRequest userPasswordRequest) {
        if (userPasswordRequest == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        String oldPassword = userPasswordRequest.getOldPassword();
        String newPassword = userPasswordRequest.getNewPassword();
        String confirmPassword = userPasswordRequest.getConfirmPassword();
        if (StringUtils.isAnyBlank(oldPassword, newPassword,confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码为空");
        }
        if (!newPassword.equals(confirmPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        if (newPassword.length() < UserConstant.PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        return ResultUtils.success(userService.updatePassword(oldPassword, newPassword));
    }
}
