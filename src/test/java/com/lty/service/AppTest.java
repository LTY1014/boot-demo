package com.lty.service;

import com.lty.mapper.BookMapper;
import com.lty.model.dto.book.BookQueryRequest;
import com.lty.model.entity.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author lty
 */
@RunWith(SpringRunner.class)
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
        dto.setAuthors(new ArrayList<>(Arrays.asList("author10", "author11")));
        List<Book> books = bookMapper.selectBookList(dto);
        System.out.println(books);
    }
}