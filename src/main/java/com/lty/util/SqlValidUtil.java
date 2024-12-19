package com.lty.util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * sql校验
 * @author lty
 */
public class SqlValidUtil {

    /**
     * 检查sql是否正确
     * @param sql
     * @return
     */
    public static boolean validSql(String sql) {
        try {
            CCJSqlParserUtil.parse(sql);
            return true;
        } catch (JSQLParserException e) {
            return false;
        }
    }

    /**
     * 校验表名是否正确
     * @param sql
     * @param pattern 正则表达式
     * @example SqlValidUtil.validSqlTableName(sql, " ittr$ ")  校验表名是否以ittr结尾
     */
    public static boolean validSqlTableName(String sql, String pattern) {
        PlainSelect plainSelect = getPlainSelect(sql);
        Table table = (Table) plainSelect.getFromItem();

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(table.getName());
        return matcher.find();
    }

    /**
     * 校验sql是否包含指定列名
     * @param sql
     * @param validColumns 必须包含的列
     */
    public static boolean validSqlColumn(String sql, List<String> validColumns) {
        try {
            PlainSelect plainSelect = getPlainSelect(sql);
            List<SelectItem> selectItems = plainSelect.getSelectItems();
            // 去除别名后的列名
            List<String> selectColumns = selectItems.stream()
                    .map(item -> item.toString().substring(item.toString().lastIndexOf(".") + 1))
                    .collect(Collectors.toList());

            return selectColumns.containsAll(validColumns);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获得数据库和表
     * @param sql
     * @return [数据库, 数据表]
     */
    public static List<String> getSchemaTable(String sql) {
        PlainSelect plainSelect = getPlainSelect(sql);
        Table table = (Table) plainSelect.getFromItem();
        return new ArrayList<>(Arrays.asList(table.getSchemaName(), table.getName()));
    }


    /**
     * 解析Sql对象
     * @param sql
     * @return
     */
    private static PlainSelect getPlainSelect(String sql) {
        try {
            if (validSql(sql)) {
                Select select = (Select) CCJSqlParserUtil.parse(sql);
                SelectBody selectBody = select.getSelectBody();
                PlainSelect plainSelect = (PlainSelect) selectBody;
                return plainSelect;
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return null;
    }
}
