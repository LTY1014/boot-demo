package com.lty.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 *
 * @author lty
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {

    @Resource
    public BookService bookService;

    @Test
    public void bookTest() {
    }
}