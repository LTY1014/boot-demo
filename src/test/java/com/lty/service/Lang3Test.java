package com.lty.service;

import com.lty.model.enums.UserRoleEnum;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.util.List;

/**
 * Apache-commons-lang3
 */
public class Lang3Test {

    @Test
    public void StringUtilsTest(){
        // 字符串缩略 abc...
        System.out.println(StringUtils.abbreviate("abcdefg", 6));
        // 如果字符串尚未以任何后缀结尾，则将后缀附加到字符串的末尾 abc.png
        System.out.println(StringUtils.appendIfMissing("abc", ".png"));
        // 字符串中字符/字符串出现次数 2
        System.out.println(StringUtils.countMatches("abba", 'a'));
        // 字符串去除空白字符 "abc"
        StringUtils.deleteWhitespace("   ab  c  ");
        // 字符串差异 "xyz"
        System.out.println(StringUtils.difference("abcde", "abxyz"));

        // 字符串是否已某个后缀结束
        StringUtils.endsWith("ABCDEF", "def"); // false
        StringUtils.endsWithAny("abcXYZ", "def", "XYZ"); // true

        // 字符串公共前缀
        StringUtils.getCommonPrefix("abcde", "abxyz"); // "ab"

        //字符串是否空，null，空白字符
        StringUtils.isBlank(" ");       // true
        StringUtils.isNotBlank(" ");    // false

        //字符串是否空，null
        StringUtils.isEmpty("");        // true
        StringUtils.isEmpty(" ");       // false
        StringUtils.isNotEmpty(" ");    // true

        //类似 trim, trim 是删除前后空格，而 trip 删除空白字符
        StringUtils.strip(" ab c "); // "ab c"
    }


    @Test
    public void SystemUtilsTest(){
        System.out.println(SystemUtils.getUserDir());  //当前项目目录
        System.out.println(SystemUtils.getHostName()); //主机名称
        System.out.println(SystemUtils.JAVA_VERSION);  // JDK版本
        System.out.println(SystemUtils.OS_NAME);
    }

    @Test
    public void enumTest(){
        UserRoleEnum admin = EnumUtils.getEnum(UserRoleEnum.class, "ADMIN");
        System.out.println(admin.getText());
        List<UserRoleEnum> enumList = EnumUtils.getEnumList(UserRoleEnum.class);
        System.out.println(enumList);
    }
}