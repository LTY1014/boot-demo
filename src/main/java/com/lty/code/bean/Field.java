package com.lty.code.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author lty
 */
@Data
@Accessors(chain = true)
public class Field {

    private String field;

    private String comment;

    private String javaType;

}
