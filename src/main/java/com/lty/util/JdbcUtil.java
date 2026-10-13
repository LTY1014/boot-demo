package com.lty.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 依赖要求 HikariCP+Spring JDBC+MySQL
 */
public class JdbcUtil {

    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/demo?serverTimezone=Asia/Shanghai&useSSL=false";
    // private static final String JDBC_SQLSERVER_URL = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=demo;encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    /**
     * 查询数据库并返回结果集
     */
    public static List<Map<String, Object>> query(String jdbcUrl, String sql, Object... args) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return readRows(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("数据库查询失败", exception);
        }
    }

    private static List<Map<String, Object>> readRows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

    public static void main(String[] args) {
        String sql = "select id, userAccount, userPassword, userRole, userName from user";
        List<Map<String, Object>> list = query(JDBC_URL, sql);
        System.out.println(list);
    }
}