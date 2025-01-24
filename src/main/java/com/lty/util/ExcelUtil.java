package com.lty.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.List;

/**
 * POI excel工具类
 */
public class ExcelUtil {

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

    /**
     * 获取单元格数据(去除空格并以字符串形式)
     * @param cell
     * @return
     */
    public static String getStringCellValue(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
