package com.lty.dao;

import com.lty.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * @author lty
 */
public interface BookDao extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // 通过字段查询
    List<Book> findByBookName(String bookName);

    // 通过注解查询
    @Query("select b from Book b where b.bookName like %?1% and b.isDelete = ?2")
    List<Book> findByBookNameAndIsDeleteIsFalse(String bookName, Integer isDelete);

    @Modifying
    @Query("update Book b set b.bookName=?2 where b.id=?1")
    void updateBookName(Long id, String bookName);
}
