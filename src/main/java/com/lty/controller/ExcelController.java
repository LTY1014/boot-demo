package com.lty.controller;

import com.alibaba.excel.EasyExcel;
import com.lty.common.ErrorCode;
import com.lty.exception.BusinessException;
import com.lty.model.dto.ExcelDemo;
import com.lty.model.dto.Person;
import com.lty.model.entity.Book;
import com.lty.util.ExcelUtil;
import com.lty.util.ServletUtil;
import com.lty.util.easyexcel.ExcelDataValidator;
import com.lty.util.easyexcel.ExcelListener;
import com.lty.util.easyexcel.ExcelWriteUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author lty
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @ApiOperation(value = "导出到Excel(POI实现)", produces = "application/octet-stream")
    @PostMapping("/export")
    public void exportBooksToExcel(HttpServletResponse response) {
        // 假设这里是生成的书籍数据
        List<Person> personList = Person.getPersonList();
        // 写入输出流
        try {

            // 创建一个新的Excel工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet0");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("姓名");
            headerRow.createCell(1).setCellValue("年龄");
            headerRow.createCell(2).setCellValue("金额");
            headerRow.createCell(3).setCellValue("生日");

            // 写入数据
            int rowNum = 1;
            for (Person person : personList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(person.getName());
                row.createCell(1).setCellValue(person.getAge());
                row.createCell(2).setCellValue(person.getMoney().toString());
                row.createCell(3).setCellValue(person.getBirthday().toString());
            }

            // 设置HTTP响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = "导出到Excel_" + timestamp + ".xlsx";
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "导入到Excel(POI实现)", produces = "application/octet-stream")
    @PostMapping("/import")
    public List<Book> importBooksFromExcel(@RequestPart MultipartFile file) {
        List<Book> books = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();

            // 跳过表头
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Book book = new Book();
                    book.setId((long) row.getCell(0).getNumericCellValue());
                    book.setBookName(row.getCell(1).getStringCellValue());
                    books.add(book);
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Imported books: " + books);
        return books;
    }

    @ApiOperation(value = "导出到Excel(POI实现2)", produces = "application/octet-stream")
    @RequestMapping(value = "/import2", method = RequestMethod.POST)
    public List<Book> importBooksFromExcel2(@RequestPart MultipartFile file) {
        List<Book> books = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Iterator<Sheet> sheetIterator = workbook.iterator();
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if (!ObjectUtils.isEmpty(fileExtension) && !fileExtension.endsWith("xlsx") && !fileExtension.endsWith("xls")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件格式不正确，请上传xlsx或xls格式文件");
            }
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    // 跳过表头读取
                    if (row.getRowNum() > 0) {
                        Book book = new Book();
                        book.setId((long) row.getCell(0).getNumericCellValue());
                        book.setBookName(row.getCell(1).getStringCellValue());
                        books.add(book);
                    }
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Imported books: " + books);
        return books;
    }

    @RequestMapping(value = "/template", method = RequestMethod.POST)
    public void exportTemplateToExcel(HttpServletResponse response) {
        // 创建一个新的Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet0");
        List<String> titleList = Arrays.asList("ID", "Book Name", "Author", "Create Time", "Update Time", "Is Delete");

        ExcelUtil.writeData(sheet, titleList, 0, new ArrayList<>());

        // 写入输出流
        try {
            String fileName = "template.xlsx";
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "导出Excel(EasyExcel实现)", produces = "application/octet-stream")
    @RequestMapping(value = "/easyexcel/export", method = RequestMethod.POST)
    public void easyexcelExport() {
        HttpServletResponse response = ServletUtil.getResponse();
        // 写入输出流
        try {
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = "template.xlsx";
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            EasyExcel.write(response.getOutputStream(), ExcelDemo.class)
                    .sheet("sheet1")
                    .doWrite(getExcelDemoList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "导入Excel(EasyExcel实现)", produces = "application/octet-stream")
    @RequestMapping(value = "/easyexcel/import", method = RequestMethod.POST)
    public void easyexcelImport(@RequestPart MultipartFile file) {
        // 写入输出流
        ExcelListener excelListener = new ExcelListener(new ExcelDataValidator(), list -> System.out.println("模拟存储数据:" + list));
        try {
            EasyExcel.read(file.getInputStream(), ExcelDemo.class, excelListener).sheet(0).doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "导出Excel(EasyExcel工具实现)", produces = "application/octet-stream")
    @RequestMapping(value = "/easyexcel/util/export", method = RequestMethod.POST)
    public void easyexcelExportByUtil() throws IOException {
        ExcelWriteUtil.exportExcel(getExcelDemoList());
    }

    // 模拟ExcelDemo数据
    public List<ExcelDemo> getExcelDemoList() {
        List<ExcelDemo> excelDemoList = new ArrayList<>();
        excelDemoList.add(new ExcelDemo("1", "张三", "001"));
        excelDemoList.add(new ExcelDemo("2", "李四", "002"));
        excelDemoList.add(new ExcelDemo("3", "王五", "003"));
        return excelDemoList;
    }
}
