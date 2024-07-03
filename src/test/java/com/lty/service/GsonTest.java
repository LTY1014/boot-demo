package com.lty.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lty.model.entity.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lty
 */
public class GsonTest {

    @Test
    public void jsonTest() {
        Gson gson = new Gson();

        // 创建测试对象
        User user = getUser();
        List<User> userList = getUserList();

        System.out.println("==============Gson : Bean to Json==============");
        String jsonUser1 = gson.toJson(user);
        System.out.println(jsonUser1);

        System.out.println("==============Gson : Json to Bean===============");
        User user1FromJson = gson.fromJson(jsonUser1, User.class);
        System.out.println(user1FromJson);

        System.out.println("==============Gson : List to Json===============");
        String jsonUserList = gson.toJson(userList);
        System.out.println(jsonUserList);

        System.out.println("==============Gson : Json to List===============");
        List<User> userListFromJson = gson.fromJson(jsonUserList, new TypeToken<List<User>>() {
        }.getType());
        System.out.println(userListFromJson);
    }


    public static User getUser() {
        User u = new User();
        u.setId(1L);
        u.setUserAccount("admin");
        u.setUserName("管理员");
        return u;
    }

    public static List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User u = new User();
            u.setId(Long.valueOf(i));
            u.setUserAccount("account" + i);
            u.setUserName("name" + i);
            userList.add(u);
        }
        return userList;
    }
}
