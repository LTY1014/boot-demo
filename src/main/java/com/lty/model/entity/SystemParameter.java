package com.lty.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 系统参数
 * @author lty
 */
@Data
@Accessors(chain = true)
@TableName("system_parameter")
public class SystemParameter implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 代码
     */
    private String code;

    /**
     * 参数值
     */
    private String value;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 描述
     */
    private String description;

    /**
     * 分类
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序
     */
    private BigDecimal sortable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}