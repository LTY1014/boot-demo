package com.lty.model.dto.book;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lty
 */
@Data
public class BookUpdateRequest implements Serializable {
    private long id;

    private String bookName;

    private String author;
}
