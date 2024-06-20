package com.lty.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lty.model.entity.Book;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author lty
 */
@Data
public class BookVO implements Serializable {
    private final static Gson GSON = new Gson();

    private String bookName;

    private List<String> author;

    /**
     * 对象转包装类
     * @param book
     * @return
     */
    public static BookVO objToVo(Book book) {
        if (book == null) {
            return null;
        }
        BookVO bookVO = new BookVO();
        BeanUtils.copyProperties(book, bookVO);
        bookVO.setAuthor(GSON.fromJson(book.getAuthor(), new TypeToken<List<String>>() {
        }.getType()));
        return bookVO;
    }
}
