package com.lty.mapper;

import com.lty.model.dto.book.BookQueryRequest;
import com.lty.model.entity.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author lty
*/
public interface BookMapper extends BaseMapper<Book> {

    List<Book> selectBookList(@Param("dto") BookQueryRequest dto);
}




