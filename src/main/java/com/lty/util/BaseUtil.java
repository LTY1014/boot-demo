package com.lty.util;

import org.openjdk.jol.vm.VM;
import org.springframework.beans.BeanUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lty
 */
public class BaseUtil {

    /**
     * 获取对象大小(单位：字节)
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> long getMemorySize(T bean) {
        return VM.current().sizeOf(bean);
    }

    /**
     * 点转斜线 (com.lty.code变为com/lty/code)
     * @param str
     * @return String
     */
    public static String dotToLine(String str) {
        return str.replace(".", "/");
    }

    // 使用序列化和反序列化实现深拷贝(注意对象要实现序列化才能实现)
    public static <T> T deepCopy(T object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(object);
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                 ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                return (T) objectInputStream.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 拷贝对象(省去创建对象)
     *
     * @param source
     * @param clazz
     * @param <V>
     * @return
     */
    public static <V> V copyBean(Object source, Class<V> clazz) {
        // 创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            // 实现属性拷贝
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 拷贝List
     *
     * @param list
     * @param clazz
     * @param <O>
     * @param <V>
     * @return
     */
    public static <O, V> List<V> copyBeanList(List<O> list, Class<V> clazz) {
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }

}
