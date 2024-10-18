package com.lty.service.java;

import com.lty.model.entity.User;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 反射
 */
public class ReflectionTest {

    // 原生反射方法
    @Test
    public void test() {
        Class<User> objectClass = User.class;
        try {
            //通过构造器创建实例
            Constructor<User> constructor = objectClass.getConstructor();
            User instance = constructor.newInstance();

            //调用方法
            Method setNameMethod = objectClass.getDeclaredMethod("setUserName", String.class);
            setNameMethod.invoke(instance, "后端java");
            //修改字段
            Field field = objectClass.getDeclaredField("phone");
            field.setAccessible(true);
            field.set(instance, "13812345678");
            System.out.println(instance);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException |
                NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // 反射工具类ReflectionUtils
    @Test
    public void test2() {
        Constructor<User> constructor = null;
        User instance = null;
        try {
            //使用构造创建实例
            constructor = ReflectionUtils.accessibleConstructor(User.class);
            instance = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //找到方法并调用
        Method setNameMethod = ReflectionUtils.findMethod(User.class, "setUserName", String.class);
        if (Objects.nonNull(setNameMethod)) {
            ReflectionUtils.invokeMethod(setNameMethod, instance, "后端java");
        }

        //找到字段设置值
        Field field = ReflectionUtils.findField(User.class, "phone");
        if (Objects.nonNull(field)) {
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, instance, "13812345678");
        }
        System.out.println(instance);
    }
}
