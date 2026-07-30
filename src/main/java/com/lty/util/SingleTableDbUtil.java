package com.lty.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

/**
 * 依赖要求 HikariCP+Spring JDBC+MySQL
 */
public class SingleTableDbUtil {

    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/demo?serverTimezone=Asia/Shanghai&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static List<Map<String, Object>> query(String sql, Object... args) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMinimumIdle(0); // 最小空闲连接数
        config.setMaximumPoolSize(2); // 最大连接数
        config.setIdleTimeout(30000); // 空闲连接超时时间
        config.setConnectionTimeout(10000); // 连接超时时间

        HikariDataSource ds = new HikariDataSource(config);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
            return jdbcTemplate.queryForList(sql, args);
        } finally {
            ds.close();
        }
    }

    public static void main(String[] args) {
        String sql = "select id, userAccount, userPassword, userRole, userName from user where id = ?";
        List<Map<String, Object>> list = query(sql,1);
        System.out.println(list);
    }
}