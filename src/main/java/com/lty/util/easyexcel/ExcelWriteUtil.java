package com.lty.util.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.lty.util.ServletUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * excel导出工具
 *
 * @author lty
 */
public class ExcelWriteUtil {

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
