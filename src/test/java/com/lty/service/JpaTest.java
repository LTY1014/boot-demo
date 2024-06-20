package com.lty.service;

import com.lty.dao.BookDao;
import com.lty.model.dto.book.BookQueryRequest;
import com.lty.model.entity.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 *
 * @author lty
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaTest {

    @Resource
    public BookService bookService;

    @Resource
    public BookDao bookDao;

    @Test
    public void bookTest() {
        BookQueryRequest bookQueryRequest = new BookQueryRequest();
        bookQueryRequest.setBookName("name1");
        Page<Book> byPage = bookService.findByPage(bookQueryRequest, 0, 5);
        // 输出结果
        System.out.println(byPage.getContent());
    }

    @Test
    public void bookTest2() {
        Book book = bookDao.findById(1L).orElse(null);
        book.setBookName("n1");
        Book newBook = new Book();
        newBook.setBookName("jpa");
        newBook.setAuthor("japAuthor");
        newBook.setCreateTime(new Date());
        newBook.setUpdateTime(new Date());
        newBook.setIsDelete(0);
        bookDao.saveAndFlush(newBook);
    }
}