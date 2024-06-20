package com.lty.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lty.model.dto.book.BookQueryRequest;
import com.lty.model.entity.Book;
import org.springframework.data.domain.Page;

/**
 * @author lty
 */
public interface BookService extends IService<Book> {

    /**
     * 校验
     * @param book
     * @param add
     */
    void validBook(Book book, boolean add);

    /**
     * 获取查询条件
     * @param bookQueryRequest
     * @return
     */
    QueryWrapper<Book> getQueryWrapper(BookQueryRequest bookQueryRequest);

    /**
     * 分页查询用户数据
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Book> findByPage(BookQueryRequest bookQueryRequest, Integer pageNum, Integer pageSize);
}
