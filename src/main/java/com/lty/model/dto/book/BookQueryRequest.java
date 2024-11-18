package com.lty.model.dto.book;

import com.lty.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author lty
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BookQueryRequest extends PageRequest {

    private String bookName;

    private String author;

    private String keyword;

    private List<String> authors;
}
