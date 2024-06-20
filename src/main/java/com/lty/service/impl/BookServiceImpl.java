package com.lty.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lty.common.ErrorCode;
import com.lty.constant.BaseConstant;
import com.lty.dao.BookDao;
import com.lty.exception.BusinessException;
import com.lty.mapper.BookMapper;
import com.lty.model.dto.book.BookQueryRequest;
import com.lty.model.entity.Book;
import com.lty.service.BookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lty
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book>
        implements BookService {

    @Resource
    private BookDao bookDao;

    @Override
    public Page<Book> findByPage(BookQueryRequest bookQueryRequest, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return bookDao.findAll(getSpecification(bookQueryRequest), pageable);
    }

    private Specification<Book> getSpecification(BookQueryRequest bookQueryRequest) {
        return new Specification<Book>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<Long> bookIdField = root.get("id");
                Path<String> bookNameField = root.get("bookName");

                List<Predicate> predicates = new ArrayList<>();
                if (StrUtil.isNotBlank(bookQueryRequest.getBookName())) {
                    predicates.add(cb.equal(bookNameField, bookQueryRequest.getBookName()));
                }
                Predicate[] arr = new Predicate[predicates.size()];
                query.where(predicates.toArray(arr));
                return null;
            }
        };
    }

    @Override
    public void validBook(Book book, boolean add) {
        if (book == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // TODO 添加要验证的字段
        Long id = book.getId();
        String bookName = book.getBookName();

        if (add) {
            if (StringUtils.isAnyBlank(bookName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }

    @Override
    public QueryWrapper<Book> getQueryWrapper(BookQueryRequest bookQueryRequest) {
        if (bookQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Book bookQuery = new Book();
        BeanUtils.copyProperties(bookQueryRequest, bookQuery);
        String sortField = bookQueryRequest.getSortField();
        String sortOrder = bookQueryRequest.getSortOrder();
        QueryWrapper<Book> qw = new QueryWrapper<>();
        // TODO 添加字段支持搜索
        String bookName = bookQuery.getBookName();
        String author = bookQuery.getAuthor();

        // TODO 添加查询条件
        qw.like(StringUtils.isNoneBlank(bookName), "bookName", bookName);
        qw.eq(StringUtils.isNoneBlank(author), "author", author);

        qw.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(BaseConstant.SORT_ORDER_ASC), sortField);
        return qw;
    }
}




