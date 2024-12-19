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
import java.util.List;
import java.util.Map;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class EasyExcelTest {

    @Test
    public void test() {
        String filename = "D:\\Desktop\\test.xlsx";
        List<Map<Integer, String>> totalList =
                EasyExcel.read(filename).sheet().headRowNumber(0).doReadSync();
        System.out.println(totalList);
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