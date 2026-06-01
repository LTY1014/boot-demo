package com.lty.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.lty.model.dto.ExcelDemo;
import com.lty.util.GrammarUtil;
import com.lty.util.easyexcel.ExcelDataValidator;
import com.lty.util.easyexcel.ExcelListener;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class EasyExcelTest {

    @Test
    public void test() {
        String filename = "C:\\Users\\Administrator\\Desktop\\1.xlsx";
        List<Map<Integer, String>> readList =
                EasyExcel.read(filename).sheet("Sheet1").headRowNumber(0).doReadSync();
        // 表头映射(表头不要重复)
        Map<Integer, String> headerMapping = new HashMap<>();
        // 结果集
        List<Map<String, String>> resultList = new ArrayList<>();
        if (readList != null && !readList.isEmpty()) {
            headerMapping.putAll(readList.get(0));
            for (Map<Integer, String> rowData : readList) {
                Map<String, String> rowMap = new HashMap<>();
                for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                    String headerKey = headerMapping.get(entry.getKey());
                    if (headerKey != null && !headerKey.trim().isEmpty()) {
                        rowMap.put(headerKey, entry.getValue());
                    }
                }
                // TODO 可添加校验逻辑是否加入结果集
                resultList.add(rowMap);
            }
        }
        System.out.println(resultList.size());
    }

    @Test
    public void writeTest() {
        // 表头映射
        Map<Integer, String> headerMapping = new HashMap<>();
        headerMapping.put(0, "序号");
        headerMapping.put(1, "项目编号");
        headerMapping.put(2, "项目名称");
        // 结果集
        List<Map<String, String>> resultList = new ArrayList<>();
        resultList.add(new HashMap<String, String>() {{
            put("序号", "1");
            put("项目编号", "T-123456");
            put("项目名称", "项目1");
        }});
        resultList.add(new HashMap<String, String>() {{
            put("序号", "2");
            put("项目编号", "T-123456");
            put("项目名称", "项目2");
        }});
        // 输出文件路径（可自行修改）
        String outputPath = "C:\\Users\\Administrator\\Desktop\\输出结果.xlsx";
        String sheetName = "Sheet1";

        // 1. 【关键】按列索引排序表头，保证列顺序和读取时完全一致，零错位
        List<Map.Entry<Integer, String>> sortedHeader = headerMapping.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 按列索引升序排序
                .collect(Collectors.toList());
        // 2. 提取表头标题列表（Excel第一行）
        List<List<String>> headerList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : sortedHeader) {
            headerList.add(Collections.singletonList(entry.getValue()));
        }
        // 3. 组装数据行：按表头顺序匹配值，保证列对应零错位
        List<List<String>> dataList = new ArrayList<>();
        for (Map<String, String> rowData : resultList) {
            List<String> row = new ArrayList<>();
            for (Map.Entry<Integer, String> header : sortedHeader) {
                // 按表头标题匹配值，空值填空字符串，避免null
                String value = rowData.getOrDefault(header.getValue(), "");
                row.add(value);
            }
            dataList.add(row);
        }
        // 4. 【核心】EasyExcel写入Excel,自动关闭流
        EasyExcel.write(outputPath)
                .sheet(sheetName)
                .head(headerList) // 写入表头
                .doWrite(dataList); // 写入数据
        System.out.println("✅ Excel写入完成！文件路径：" + outputPath);
        System.out.println("✅ 共写入 " + headerList.size() + " 列，" + dataList.size() + " 行数据");
    }

    /**
     * @method 通过Excel生成SQL
     * @remark 1.配置Excel文件路径，2.配置表名 3.配置Excel导入对象 4.配置忽略字段(可选)
     */
    @Test
    public void insertSqlByExcel() {
        String filename = "D:\\Desktop\\demo_book.xlsx";
        List<ExcelDemo> list =
                EasyExcel.read(filename).excelType(ExcelTypeEnum.XLSX).head(ExcelDemo.class).sheet("Result 1").doReadSync();

        Field[] fields = FieldUtils.getAllFields(ExcelDemo.class);

        // 忽略字段
        List<String> ignoreField = new ArrayList<>(Arrays.asList("serialVersionUID", "isDelete"));
        // 表名
        String tableName = "demo.book";

        // 字段名
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (ignoreField.contains(fieldName)) {
                continue;
            }
            fieldNames.add(fieldName);
        }

        for (ExcelDemo demo : list) {
            // 为每个对象创建新的字段值列表
            List<Object> fieldValues = new ArrayList<>();
            for (String fieldName : fieldNames) {
                try {
                    // 使用反射找到对应的 getter 方法
                    Method getter = ExcelDemo.class.getMethod("get" + GrammarUtil.isFirstUpper(fieldName, true));
                    // 调用 getter 方法获取值
                    Object value = getter.invoke(demo);
                    // 将值添加到字段值列表中
                    fieldValues.add(value);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    // 处理异常，比如记录日志或抛出运行时异常
                    throw new RuntimeException("无法获取字段 " + fieldName + " 的值", e);
                }
            }

            // 生成 SQL 语句
            StringBuilder insertSql = new StringBuilder("INSERT INTO ")
                    .append(tableName)
                    .append(" (")
                    .append(String.join(", ", fieldNames))
                    .append(") VALUES (")
                    .append(String.join(", ", fieldValues.stream().map(Object::toString).toArray(String[]::new)))
                    .append(");");
            System.out.println(insertSql.toString());
        }
    }

        // 测试ExcelListener
    @Test
    public void ExcelListener() {
        String filename = "D:\\Desktop\\customer_product.xlsx";
        ExcelListener excelListener = new ExcelListener(new ExcelDataValidator());
        EasyExcel.read(filename, ExcelDemo.class, excelListener).sheet("template").doRead();
        List list = excelListener.getData();
    }
}