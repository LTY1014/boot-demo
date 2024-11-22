package com.lty.service.java;

import com.lty.model.entity.User;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Test
    public void getField() {
        // 假设你知道类的全限定名(替换为实际的类名，注意使用点(.)分隔包名和类名)
        String fullyQualifiedName = "com.lty.model.entity.User";
        // 忽略字段
        List<String> ignoreField = new ArrayList<>(Arrays.asList("serialVersionUID", "isDelete"));

        try {
            // 使用默认的类加载器加载类
            Class<?> clazz = Class.forName(fullyQualifiedName);
            // 获取无参构造器（假设有一个无参构造器）
            Constructor<?> constructor = clazz.getConstructor();
            // 创建类的实例（虽然在这个方法中我们并没有使用到这个实例）
            Object instance = constructor.newInstance();
            // 输出类名，确认实例已创建（可选）
            System.out.println("Instance of " + clazz.getName() + " created.");

            // 获取类的所有字段（包括私有字段），注意这里使用FieldUtil.getAllFields(clazz)
            Field[] fields = FieldUtils.getAllFields(clazz);

            for (Field field : fields) {
                // 获取字段名称
                String fieldName = field.getName();
                // 获取字段类型
                Class<?> fieldType = field.getType();
                // 跳过忽略字段
                if (ignoreField.contains(fieldName)) {
                    continue;
                }
                // 后续操作：如输出字段名称和类型
                System.out.println("Field Name: " + fieldName + ", Field Type: " + fieldType.getName());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.err.println("No such method (constructor): " + e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("Cannot instantiate class: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Invocation target exception: " + e.getCause().getMessage());
        }
    }
}
