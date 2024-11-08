package com.lty.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * excel工具类
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
     * 获取单元格数据(去除空格并以字符串形式)
     * @param cell
     * @return
     */
    public static String getStringCellValue(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
