package com.lty.code.bean;

import lombok.Data;

/**
 * @author lty
 */
@Data
public class Entity {

    private String entityPackage;

    private String daoPackage;

    private String servicePackage;

    private String serviceImplPackage;

    private String controllerPackage;

    private String author;

    private String className;

    private String classNameLowerCase;

    private String tableName;

    private String description;

    private String primaryKeyType;

    private boolean commonFiled;

}
