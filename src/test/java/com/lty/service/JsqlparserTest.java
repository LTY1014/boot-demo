package com.lty.service;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * jsqlparser 解析和操作 SQL 语句
 */
public class JsqlparserTest {

    @Test
    public void test() throws Exception {
        try {
            List<String> insertStatements = List.of(
                    "INSERT INTO my_table (col1, col2) VALUES (1, 'a');",
                    "INSERT INTO my_table (col1, col2) VALUES (2, 'b');",
                    "INSERT INTO my_table (col1, col2) VALUES (3, 'c');"
            );

            String mergedInsert = mergeInsertStatements(insertStatements);
            System.out.println(mergedInsert);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 合并多个 INSERT 语句
    public static String mergeInsertStatements(List<String> insertStatements) throws Exception {
        Insert baseInsert = null; // 用于存储合并后的语句
        List<ExpressionList> allValues = new ArrayList<>(); // 用于存储所有 VALUES

        for (String statement : insertStatements) {
            // 解析 INSERT 语句
            Insert insert = (Insert) CCJSqlParserUtil.parse(statement);

            if (baseInsert == null) {
                // 初始化 baseInsert
                baseInsert = insert;
            }

            // 获取 VALUES 并添加到列表中
            if (insert.getItemsList() instanceof ExpressionList) {
                allValues.add((ExpressionList) insert.getItemsList());
            } else if (insert.getItemsList() instanceof ValuesStatement) {
                ValuesStatement valuesStatement = (ValuesStatement) insert.getItemsList();
                PlainSelect plainSelect = (PlainSelect) ((SubSelect) valuesStatement.getExpressions()).getSelectBody();
                allValues.add((ExpressionList) plainSelect.getSelectItems());
            }
        }

        // 将所有 VALUES 合并到 baseInsert 中
        if (baseInsert != null) {
            MultiExpressionList multiExpressionList = new MultiExpressionList();
            multiExpressionList.setExpressionLists(allValues);
            baseInsert.setItemsList(multiExpressionList);
            return baseInsert.toString();
        }

        return null;
    }
}
