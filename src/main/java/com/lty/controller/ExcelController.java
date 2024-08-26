package com.lty.controller;

import com.lty.model.entity.Book;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author lty
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @ApiOperation(value = "导出到Excel", produces = "application/octet-stream")
    @GetMapping("/export")
    public void exportBooksToExcel(HttpServletResponse response) {
        // 假设这里是生成的书籍数据
        List<Book> books = generateDummyBooks();

        // 创建一个新的Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");

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

        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=books.xlsx");

        // 写入输出流
        try {
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

    @GetMapping("/template")
    public void exportTemplateToExcel(HttpServletResponse response) {
        // 创建一个新的Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet");
        List<String> titleList = Arrays.asList("ID", "Book Name", "Author", "Create Time", "Update Time", "Is Delete");

        writeCell(sheet, 0, titleList);

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

    /**
     * 写入标题
     * @param sheet
     * @param rowNum 行号默认0开始
     * @param cellValue
     */
    private void writeCell(Sheet sheet, int rowNum, List<String> cellValue) {
        Row titleRow = sheet.createRow(rowNum);
        for (int i = 0; i < cellValue.size(); i++) {
            titleRow.createCell(i).setCellValue(cellValue.get(i));
        }
    }

    /**
     * 获取单元格中的字符串值
     * @param cell
     * @return
     */
    public static String getStringCellValue(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    // 这里假设一个简单的数据生成方法
    private List<Book> generateDummyBooks() {
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
