package com.lty.service;

import com.lty.code.bean.Entity;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.junit.Test;

import java.io.IOException;

/**
 * 代码生成测试
 * @author lty
 */
public class BeetlTest {

    @Test
    public void Test() throws IOException {
        // 方式一：模板路径(从resource目录下)
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/btl/");
        Configuration cfg = Configuration.defaultConfiguration();

        // 方式二：模板字符串
        //StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        //Configuration cfg = Configuration.defaultConfiguration();

        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);

        // 模板绑定
        Template controllerTemplate = gt.getTemplate("mpController.btl");
        System.out.println(getTemplate(controllerTemplate));
    }

    private static String getTemplate(Template template) {
        final String varEntity = "entity";
        template.binding(varEntity, getEntity());
        String result = template.render();
        return result;
    }

    private static Entity getEntity() {
        Entity entity = new Entity();
        entity.setEntityPackage("com.lty.model");
        entity.setDaoPackage("com.lty");
        entity.setServicePackage("com.lty");
        entity.setServiceImplPackage("com.lty");
        entity.setControllerPackage("com.lty.controller");
        entity.setAuthor("lty");
        entity.setClassName("Suppliers");
        entity.setClassNameLowerCase("suppliers");
        entity.setTableName("");
        entity.setDescription("供应商管理");
        entity.setPrimaryKeyType("String");
        return entity;
    }
}
