package com.lty.model.dto.book;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lty
 */
@Data
public class BookAddRequest implements Serializable {

    private String bookName;

    private String author;
}
