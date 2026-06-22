package com.lty.util;

import com.alibaba.excel.EasyExcel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelPoiUtil {

    /**
     * 读取Excel文件并转换为列表格式
     *
     * @param filePath Excel文件路径
     * @param sheetName 工作表名称
     * @return 转换后的数据列表，每行数据以Map形式存储，key为表头名称，value为单元格值
     */
    public static List<Map<String, String>> readExcelWithHeaderMapping(String filePath, String sheetName) {
        List<Map<Integer, String>> readList =
                EasyExcel.read(filePath).sheet(sheetName).headRowNumber(0).doReadSync();

        // 表头映射(表头不要重复)
        Map<Integer, String> headerMapping = new HashMap<>();
        // 结果集
        List<Map<String, String>> resultList = new ArrayList<>();

        if (readList != null && !readList.isEmpty()) {
            headerMapping.putAll(readList.get(0));
            for (int i = 1; i < readList.size(); i++) { // 从第二行开始处理数据，跳过表头
                Map<Integer, String> rowData = readList.get(i);
                Map<String, String> rowMap = new HashMap<>();
                for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                    String headerKey = headerMapping.get(entry.getKey());
                    if (headerKey != null && !headerKey.trim().isEmpty()) {
                        rowMap.put(headerKey, entry.getValue());
                    }
                }
                resultList.add(rowMap);
            }
        }

        return resultList;
    }

    // 导出到Excel(EasyExcel实现)
    public static void exportExcel(List<? extends Object> dataList, String fileName, String sheetName) throws IOException {
        HttpServletResponse response = ServletUtil.getResponse();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), dataList.get(0).getClass()).sheet(sheetName).doWrite(dataList);
    }

    /**
     * 写入数据
     * @param sheet
     * @param dataValue
     * @param dataIndex 数据行下标
     * @param redIndex 符合条件的列下标，字体为红色
     */
    public static void writeData(Sheet sheet, List<String> dataValue, Integer dataIndex, List<Integer> redIndex) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle redFontCellStyle = workbook.createCellStyle();
        Font redFont = workbook.createFont();
        redFont.setColor(IndexedColors.RED.getIndex());
        redFontCellStyle.setFont(redFont);

        Row titleRow = sheet.createRow(dataIndex);
        for (int i = 0; i < dataValue.size(); i++) {
            Cell cell = titleRow.createCell(i);
            if (redIndex.contains(i)) {
                cell.setCellStyle(redFontCellStyle);
            }
            cell.setCellValue(dataValue.get(i));
        }
    }

    /**
     * 写入数据List
     * @param sheet
     * @param dataIndex 数据行下标(0标头,1数据开始)
     * @param dataValue 数据List
     * @param key 数据List对应的字段属性(根据顺序渲染)
     * @example ExcelUtil.writeDataList(sheet, rowNum, list, Arrays.asList("categoryName", "categoryCode", "effectiveDate"));
     */
    public static <T> void writeDataList(Sheet sheet, Integer dataIndex, List<T> dataValue, List<String> key) {
        for (int i = 0; i < dataValue.size(); i++) {
            Row row = sheet.createRow(dataIndex + i);
            T item = dataValue.get(i);
            for (int j = 0; j < key.size(); j++) {
                Cell cell = row.createCell(j);
                String fieldName = key.get(j);
                try {
                    Field field = item.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(item);
                    cell.setCellValue(value != null ? value.toString() : "");
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    cell.setCellValue("");
                }
            }
        }
    }
}