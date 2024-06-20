package com.lty.code;

import com.lty.constant.BaseConstant;

import java.io.File;

/**
 * 项目初始化
 * @author lty
 */
public class ProjectInit {

    public static void main(String[] args) {
        // 删除多余文件
        deleteCode();
    }

    public static void deleteCode() {
        try {
            String indexControllerPath = BaseConstant.PROJECT_ROOT_DIRECTORY + "/src/main/java/com/lty/controller/IndexController.java";
            String bookVOPath = BaseConstant.PROJECT_ROOT_DIRECTORY + "/src/main/java/com/lty/model/vo/BookVO.java";

            File indexControllerFile = new File(indexControllerPath);
            File bookVOFile = new File(bookVOPath);

            if (indexControllerFile.exists()) {
                indexControllerFile.delete();
            }
            if (bookVOFile.exists()) {
                bookVOFile.delete();
            }
            MybatisGenerate.deleteCode("Book");
            System.out.println("项目初始化成功...");
        } catch (Exception e) {
            System.out.println("项目初始化失败...");
            e.printStackTrace();
        }
    }
}
