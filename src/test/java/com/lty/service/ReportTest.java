package com.lty.service;

import net.sf.jasperreports.engine.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

// 假设存在Person类和其他必要的类和资源

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportTest {

    private static final String REPORT_JRXML_PATH = "user_report.jrxml";
    private static final String REPORT_OUTPUT_PATH = "user_report.pdf";


    @Test
    public void Test() {
        try {
            // 从类路径加载 JasperReport 模板
            InputStream reportTemplate = ReportTest.class.getClassLoader().getResourceAsStream(REPORT_JRXML_PATH);

            if (reportTemplate == null) {
                throw new IllegalArgumentException("Unable to load the report template.");
            }

            // 编译 JasperReport 模板
            JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate);

            // 方式一：准备数据源（此处简化，实际应用应从数据库或其他来源动态获取数据）
            //List<User> dataList = userService.list();
            //JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

            // 方式二：创建数据库连接
            String url = "jdbc:mysql://localhost:3306/demo";
            String user = "root";
            String password = "123456";
            Connection connection = DriverManager.getConnection(url, user, password);

            // 填充报表
            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // 导出报表为 PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, REPORT_OUTPUT_PATH);

            System.out.println("Report generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
