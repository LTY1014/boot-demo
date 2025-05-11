package com.lty.model.dto.book;

import com.lty.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lty
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BookQueryRequest extends PageRequest {

    private String bookName;

    /**
     * 类型
     */
    private String type;

    private List<String> typeList;

    private String keyword;

    /**
     * 价格
     */
    private BigDecimal price;
}
