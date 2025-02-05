package com.lty.service;

import com.lty.code.bean.Field;
import com.lty.util.BaseUtil;
import com.lty.util.GrammarUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
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

    public static String generateEntityFromCreateTable(String createTableSql) throws Exception {
        // 解析建表语句
        CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(createTableSql);

        // 表名
        String tableName = createTable.getTable().getName();

        // 字段定义
        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();

        // 构建实体类
        StringBuilder entityClass = new StringBuilder();
        entityClass.append("public class ").append(GrammarUtil.isFirstUpper(tableName, true)).append(" {\n\n");

        for (ColumnDefinition column : columnDefinitions) {
            String columnName = column.getColumnName();
            String dataType = column.getColDataType().getDataType();
            String columnComment = column.getColumnSpecs().get(2);
            String javaType = GrammarUtil.mapSqlTypeToJavaType(dataType);

            // 添加字段
            entityClass.append("    private ").append(javaType).append(" ").append(GrammarUtil.isFirstUpper(columnName, false)).append(";\n");
        }

        entityClass.append("\n}");
        return entityClass.toString();
    }



    @Test
    public void test2() throws Exception {
        String createTableSql = """
                create table log
                (
                    id         varchar(255)                       not null comment 'id'
                        primary key,
                    ip         varchar(256)                       null comment 'ip地址',
                    path       varchar(256)                       null comment '请求路径',
                    params     varchar(512)                       null comment '请求参数',
                    cost       bigint                             null comment '请求耗时(ms)',
                    type       varchar(256)                       null comment '请求类型',
                    userId     bigint                             null comment '用户Id',
                    client     varchar(256)                       null comment '客户端',
                    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
                    isDelete   tinyint  default 0                 not null comment '是否删除'
                )
                    comment '日志';
                """;
        String entityClass = generateEntityFromCreateTable(createTableSql);
        System.out.println(entityClass);
    }

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

    //  ===============================================
    public String templateStr = """
            create table project
            (
                id          bigint auto_increment comment 'id'
                    primary key,
                code        varchar(64)                             not null comment '标识',
                name        varchar(256)                            null comment '名称',
                description varchar(256)                            null comment '描述',
                sortable    decimal(5, 2) default 100.00            null comment '排序',
                status      int           default 1                 null comment '状态:0-禁用,1-启用',
                createTime  datetime      default CURRENT_TIMESTAMP null comment '创建时间',
                updateTime  datetime      default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
                isDelete    tinyint       default 0                 not null comment '是否删除'
            )
                comment '项目表' charset = utf8mb4;
                        """;

    // 测试根据SQL语句得到字段信息
    @Test
    public void testField() throws JSQLParserException {
        // 根据templateStr得到List<FieldInfo> fieldInfoList 其中有（字段名，字段类型，是否为空，是否主键，是否自增）
        List<Field> fieldInfoList = parseSqlFields(templateStr);
        System.out.println(fieldInfoList);
    }


    private static List<Field> parseSqlFields(String sql) throws JSQLParserException {
        List<Field> fieldInfos = new ArrayList<>();
        CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sql);
        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();

        for (ColumnDefinition columnDefinition : columnDefinitions) {
            Field fieldInfo = new Field();
            String columnName = columnDefinition.getColumnName();
            String dataType = columnDefinition.getColDataType().getDataType();
            List<String> columnSpecs = columnDefinition.getColumnSpecs();
            String columnComment = "";
            // 提取注释内容
            if (columnSpecs != null) {
                for (int i = 0; i < columnSpecs.size(); i++) {
                    String spec = columnSpecs.get(i);
                    if ("COMMENT".equalsIgnoreCase(spec)) {
                        if (i + 1 < columnSpecs.size()) {
                            String commentValue = columnSpecs.get(i + 1);
                            // 去除首尾的单引号
                            columnComment = commentValue.replaceAll("^'|'$", "");
                            break;
                        }
                    }
                }
            }
            String javaType = GrammarUtil.mapSqlTypeToJavaType(dataType);

            fieldInfo.setField(columnName);
            fieldInfo.setJavaType(javaType);
            fieldInfo.setComment(columnComment);
            fieldInfos.add(fieldInfo);
        }

        return fieldInfos;
    }
}
