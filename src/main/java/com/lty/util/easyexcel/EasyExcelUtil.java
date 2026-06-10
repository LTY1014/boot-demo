package com.lty.util.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.lty.util.ServletUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel工具类
 */
public class EasyExcelUtil {

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

    public static void exportExcel(List<? extends Object> dataList) throws IOException {
        HttpServletResponse response = ServletUtil.getResponse();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(String.valueOf(System.currentTimeMillis()), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), dataList.get(0).getClass()).sheet("sheet1").doWrite(dataList);
    }

    public static void exportExcel(List<? extends Object> dataList, String fileName, String sheetName) throws IOException {
        HttpServletResponse response = ServletUtil.getResponse();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), dataList.get(0).getClass()).sheet(sheetName).doWrite(dataList);
    }
}
