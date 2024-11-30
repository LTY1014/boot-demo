package com.lty.service.java;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertStatementCombiner {

    /**
     * 从文本中提取 INSERT 语句，并将其合并成一个语句
     * @param inputText
     * @return
     */
    public static String combineInsertStatementsFromText(String inputText) {
        // 正则表达式匹配带列名的 INSERT 语句
        String regexWithColumns = "insert\\s+into\\s+(\\S+)\\s*\\((.+?)\\)\\s*values\\s*\\((.+?)\\);";
        Pattern patternWithColumns = Pattern.compile(regexWithColumns, Pattern.CASE_INSENSITIVE);

        // 正则表达式匹配不带列名的 INSERT 语句
        String regexWithoutColumns = "insert\\s+into\\s+(\\S+)\\s*values\\s*\\((.+?)\\);";
        Pattern patternWithoutColumns = Pattern.compile(regexWithoutColumns, Pattern.CASE_INSENSITIVE);

        String tableName = "";
        String columns = "";
        List<String> valuesList = new ArrayList<>();
        boolean foundColumns = false; // 是否找到了列名

        // 将输入文本按行拆分
        String[] statements = inputText.split("\\n");

        for (String statement : statements) {
            statement = statement.trim();
            if (statement.isEmpty()) {
                continue; // 跳过空行
            }

            Matcher matcherWithColumns = patternWithColumns.matcher(statement);
            if (matcherWithColumns.matches()) {
                if (tableName.isEmpty()) {
                    tableName = matcherWithColumns.group(1); // 表名
                    columns = matcherWithColumns.group(2); // 列名
                    foundColumns = true;
                }
                valuesList.add("  (" + matcherWithColumns.group(3) + ")"); // 取出值，并加上缩进
            } else {
                Matcher matcherWithoutColumns = patternWithoutColumns.matcher(statement);
                if (matcherWithoutColumns.matches()) {
                    if (tableName.isEmpty()) {
                        tableName = matcherWithoutColumns.group(1); // 表名
                    }
                    valuesList.add("  (" + matcherWithoutColumns.group(2) + ")"); // 取出值，并加上缩进
                }
            }
        }

        // 合并成一个插入语句
        if (!valuesList.isEmpty()) {
            String columnsPart = foundColumns ? "(" + columns + ")" : "";
            return "INSERT INTO " + tableName + " " + columnsPart + "\nVALUES\n" + String.join(",\n", valuesList) + ";";
        } else {
            return "未找到有效的 INSERT 语句。";
        }
    }
}
