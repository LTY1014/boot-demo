package com.lty.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lty.common.*;
import com.lty.exception.BusinessException;

import com.lty.model.entity.SystemParameter;
import com.lty.service.SystemParameterService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统参数
 * @author lty
 */
@Slf4j
@RestController
@RequestMapping("/systemParameter")
public class SystemParameterController {

    @Resource
    private SystemParameterService systemParameterService;

    @ApiOperation(value = "添加")
    @PostMapping("/add")
    public BaseResponse<Long> addSystemParameter(@RequestBody SystemParameter systemParameterAddRequest) {
        if (systemParameterAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SystemParameter systemParameter = new SystemParameter();
        BeanUtils.copyProperties(systemParameterAddRequest, systemParameter);
        boolean result = systemParameterService.save(systemParameter);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(systemParameter.getId());
    }


    @ApiOperation(value = "修改")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateSystemParameter(@RequestBody SystemParameter systemParameterUpdateRequest) {
        if (systemParameterUpdateRequest == null || systemParameterUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        SystemParameter systemParameter = new SystemParameter();
        BeanUtils.copyProperties(systemParameterUpdateRequest, systemParameter);
        SystemParameter oldSystemParameter = systemParameterService.getById(systemParameterUpdateRequest.getId());
        if (oldSystemParameter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = systemParameterService.updateById(systemParameter);
        return ResultUtils.success(result);
    }

    @ApiOperation(value = "通过id获取")
    @GetMapping(value = "/get/{id}")
    public BaseResponse<SystemParameter> getBySystemParameterId(@PathVariable Long id) {
        if (id <= 0) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SystemParameter systemParameter = systemParameterService.getById(id);
        return ResultUtils.success(systemParameter);
    }

    @ApiOperation(value = "列表获取")
    @GetMapping(value = "/list")
    public BaseResponse<List<SystemParameter>> listSystemParameter(SystemParameter systemParameter) {
        Wrapper<SystemParameter> qw = this.buildQueryWrapper(systemParameter);
        List<SystemParameter> systemParameterList = systemParameterService.list(qw);
        return ResultUtils.success(systemParameterList);
    }

    @ApiOperation(value = "分页获取")
    @GetMapping(value = "/list/page")
    public BaseResponse<Page<SystemParameter>> listSystemParameterByPage(PageRequest pageRequest, SystemParameter systemParameter) {
        // 分页基本字段
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        // 反爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "查询数量过多");
        }
        Page<SystemParameter> page = new Page<>(current,size);
        Page<SystemParameter> systemParameterPage = systemParameterService.page(page, this.buildQueryWrapper(systemParameter));
        return ResultUtils.success(systemParameterPage);
    }

    @ApiOperation(value = "删除")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSystemParameter(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        SystemParameter oldSystemParameter = systemParameterService.getById(id);
        if (oldSystemParameter == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean b = systemParameterService.removeById(id);
        return ResultUtils.success(b);
    }
    
    public Wrapper<SystemParameter> buildQueryWrapper(SystemParameter systemParameter) {
        QueryWrapper<SystemParameter> qw = new QueryWrapper<>();

        return qw;
    }
}
