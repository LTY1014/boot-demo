package com.lty.util;

import com.lty.model.dto.Person;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 工具类:用于将对象列表转换成 Map
 * @author lty
 */
public class MappingUtil {

    /**
     * 根据多个分类器对对象转换成 Map，键是由多个分类器的结果拼接成的字符串，值是对象本身
     * @param classifiers
     * @param <T>
     * @param <K>
     * @return
     */
    @SafeVarargs
    public static <T, K> Collector<T, ?, Map<String, T>> toMapMulti(
            Function<? super T, K>... classifiers) {
        List<Function<? super T, K>> classifierList = Arrays.asList(classifiers);
        return Collectors.toMap(
                t -> classifierList.stream()
                        .map(classifier -> {
                            K result = classifier.apply(t);
                            return result != null ? String.valueOf(result) : "";
                        })
                        .collect(Collectors.joining("-")),
                Function.identity(),
                // 保证键冲突时，保留旧值
                (existing, replacement) -> existing
        );
    }

    /**
     * 根据多个分类器对对象进行分组，返回分组结果的 Map，键是拼接后的字符串，值是对应的对象列表
     * @param classifiers
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> Collector<T, ?, Map<String, List<T>>> groupingByMulti(
            Function<? super T, ?>... classifiers) {
        return Collectors.groupingBy(
                t -> Stream.of(classifiers)
                        .map(classifier -> String.valueOf(classifier.apply(t)))
                        .collect(Collectors.joining("-"))
        );
    }

    /**
     * 将多个字段拼接成字符串(用于业务键)
     * @param fields
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> String joinFields(T... fields) {
        return Arrays.stream(fields)
                .map(field -> field instanceof String ? (String) field : String.valueOf(field))
                .collect(Collectors.joining("-"));
    }

    // 防止实例化
    private MappingUtil() {
    }

    public static void main(String[] args) {
        List<Person> personList = Person.getPersonList();
        //personList.add(new Person("赵六", 22, new BigDecimal(488), LocalDate.of(2024, 7, 7)));  // 测试键冲突
        Map<String, Person> userMap = personList.stream()
                .collect(toMapMulti(Person::getName, Person::getAge));
        System.out.println(userMap);
        Map<String, List<Person>> userGroupMap = personList.stream()
                .collect(groupingByMulti(Person::getName, Person::getAge));
        System.out.println(userGroupMap);
    }
}