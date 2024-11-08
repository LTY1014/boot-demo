package com.lty.controller;

import com.lty.common.ErrorCode;
import com.lty.exception.BusinessException;
import com.lty.model.entity.Book;
import com.lty.util.ExcelUtil;
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author lty
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @ApiOperation(value = "导出到Excel", produces = "application/octet-stream")
    @PostMapping("/export")
    public void exportBooksToExcel(HttpServletResponse response) {
        // 假设这里是生成的书籍数据
        List<Book> books = generateDummyBooks();
        // 写入输出流
        try {

            // 创建一个新的Excel工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sheet0");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Book Name");
            headerRow.createCell(2).setCellValue("Author");
            headerRow.createCell(3).setCellValue("Create Time");
            headerRow.createCell(4).setCellValue("Update Time");
            headerRow.createCell(5).setCellValue("Is Delete");

            // 写入数据
            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getId());
                row.createCell(1).setCellValue(book.getBookName());
                row.createCell(2).setCellValue(book.getAuthor());
                row.createCell(3).setCellValue(book.getCreateTime().toString());
                row.createCell(4).setCellValue(book.getUpdateTime().toString());
                row.createCell(5).setCellValue(book.getIsDelete());
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
                    book.setAuthor(row.getCell(2).getStringCellValue());
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
                        book.setAuthor(row.getCell(2).getStringCellValue());
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

        ExcelUtil.writeData(sheet,  titleList,0,new ArrayList<>());

        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=template.xlsx");

        // 写入输出流
        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 这里假设一个简单的数据生成方法
    public List<Book> generateDummyBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book().setId(1L)
                .setBookName("Book A")
                .setAuthor("Author A")
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setIsDelete(0));
        books.add(new Book().setId(2L)
                .setBookName("Book B")
                .setAuthor("Author B")
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setIsDelete(0));
        return books;
    }
}
