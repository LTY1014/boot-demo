package com.lty.service;

import com.lty.mapper.BookMapper;
import com.lty.model.dto.book.BookQueryRequest;
import com.lty.model.entity.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author lty
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AppTest {

    @Resource
    public BookService bookService;

    @Resource
    public BookMapper bookMapper;

    @Test
    public void bookTest() {
        BookQueryRequest dto = new BookQueryRequest();
        //dto.setBookName("11");
        List<Book> books = bookMapper.selectBookList(dto);
        System.out.println(books);
    }
}