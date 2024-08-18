package com.lty.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lty
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person implements Serializable {

    private String name;

    private Integer age;

    private BigDecimal money;

    private LocalDate birthday;

    public static List<Person> getPersonList() {
        List<Person> personList = new ArrayList<>();
        personList.add(new Person("张三", 18, new BigDecimal(100), LocalDate.of(2024, 7, 1)));
        personList.add(new Person("李四", 18, new BigDecimal(200), LocalDate.of(2024, 7, 3)));
        personList.add(new Person("王五", 20, new BigDecimal(300), LocalDate.of(2024, 7, 5)));
        personList.add(new Person("赵六", 22, new BigDecimal(400), LocalDate.of(2024, 7, 7)));
        return personList;
    }
}
