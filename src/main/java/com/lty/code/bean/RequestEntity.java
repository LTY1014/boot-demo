package com.lty.code.bean;

import lombok.Data;

/**
 * 请求模板参照绑定对象
 *
 * @author lty
 */
@Data
public class RequestEntity {

    private String className;

    private String classNameLowerCase;

    private String author;

    private String resultPackage;

}
