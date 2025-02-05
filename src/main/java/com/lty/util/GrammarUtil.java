package com.lty.util;


import cn.hutool.core.util.StrUtil;
import com.lty.common.ErrorCode;
import com.lty.exception.BusinessException;

/**
 * 语法工具类
 * @author lty
 */
public class GrammarUtil {

    /**
     * 首字母是否大小写(true大写,false小写)
     * @param name
     * @param isFirstUpper
     * @return
     */
    public static String isFirstUpper(String name, boolean isFirstUpper) {
        if (StrUtil.isBlank(name)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "name不能为空");
        }

        if (name.length() == 1) {
            if (isFirstUpper) {
                return name.toUpperCase();
            } else {
                return name.toLowerCase();
            }
        }

        StringBuffer sb = new StringBuffer();
        if (isFirstUpper) {
            sb.append(Character.toUpperCase(name.charAt(0)));
        } else {
            sb.append(Character.toLowerCase(name.charAt(0)));
        }
        sb.append(name.substring(1));
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     */
    public static String camel2Underline(String str) {

        if (StrUtil.isBlank(str)) {
            return "";
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                sb.append("_" + Character.toLowerCase(str.charAt(i)));
            } else {
                sb.append(str.charAt(i));
            }
        }
        return (str.charAt(0) + sb.toString()).toLowerCase();
    }

    /**
     * 下划线转驼峰法
     */
    public static String underline2Camel(String str) {
        if (StrUtil.isBlank(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

    public static String mapSqlTypeToJavaType(String sqlType) {
        // 简单映射 SQL 数据类型到 Java 类型
        switch (sqlType.toLowerCase()) {
            case "varchar":
            case "char":
            case "text":
                return "String";
            case "int":
            case "integer":
            case "tinyint":
                return "Integer";
            case "bigint":
                return "Long";
            case "decimal":
            case "numeric":
                return "BigDecimal";
            case "date":
            case "datetime":
            case "timestamp":
                return "LocalDateTime";
            case "boolean":
                return "Boolean";
            default:
                return "Object"; // 默认类型
        }
    }
}
