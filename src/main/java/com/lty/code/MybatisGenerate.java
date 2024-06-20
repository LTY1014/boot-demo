package com.lty.code;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.lty.code.bean.Entity;
import com.lty.code.bean.Item;
import com.lty.constant.BaseConstant;
import com.lty.exception.BusinessException;
import com.lty.common.ErrorCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成Mybatis-plus代码
 * @author lty
 */
@Slf4j
public class MybatisGenerate {

    private static GroupTemplate gt;

    public static void main(String[] args) throws Exception {
        GroupTemplate gt = getGroupTemplate();

        // 生成代码(自行补充实体类字段)
        generateCode(gt);

        // 读取你的实体类中的字段，补充生成条件构造分页查询代码[需自行复制控制台打印输出的代码自行覆盖]
        //generatePlus(gt);

        // 根据类名删除生成的代码
        // deleteCode(CLASS_NAME);
    }

    //region 配置选项
    /** 类名(注意大写) */
    public static final String CLASS_NAME = "Student";

    /** 类描述 */
    public static final String DESCRIPTION = "学生表";

    /** 作者名 */
    public static final String AUTHOR = "lty";

    /** 是否加通用字段(createTime,updateTime,isDelete) */
    public static final boolean COMMON_Filed = false;

    /** 数据库表前缀 */
    private static final String TABLE_PRE = "";

    /** 主键类型 */
    private static final String PRIMARY_KEY_TYPE = "Long";

    /** 项目包名 */
    public static final String PROJECT_PACKAGE = "com.lty";

    /** 模块包路径 （.+下方包路径拼接使用） */
    private static final String MODULE = "";
    //private static final String MODULE = ".app";

    /** 实体类对应包 (文件自动生成至该包下) */
    private static final String ENTITY_PACKAGE = PROJECT_PACKAGE + MODULE + ".model" + ".entity";

    /** dao对应包 [注意修改后需到MybatisPlusConfig配置你的mapper路径扫描] */
    private static final String DAO_PACKAGE = PROJECT_PACKAGE + MODULE + ".mapper";

    /** service对应包 (文件自动生成至该包下) */
    private static final String SERVICE_PACKAGE = PROJECT_PACKAGE + MODULE + ".service";

    /** serviceImpl对应包 (文件自动生成至该包下) */
    private static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";

    /** controller对应包 (文件自动生成至该包下) */
    private static final String CONTROLLER_PACKAGE = PROJECT_PACKAGE + MODULE + ".controller";

    /** 路径前缀 */
    private static final String SYS_PATH = BaseConstant.PROJECT_ROOT_DIRECTORY + "/src/main/java/";
    //endregion

    /**
     * 生成代码
     * @param gt
     * @throws IOException
     */
    private static void generateCode(GroupTemplate gt) throws IOException {

        Template entityTemplate = gt.getTemplate("entity.btl");
        Template daoTemplate = gt.getTemplate("mapper.btl");
        Template serviceTemplate = gt.getTemplate("mpService.btl");
        Template serviceImplTemplate = gt.getTemplate("mpServiceImpl.btl");
        Template controllerTemplate = gt.getTemplate("mpController.btl");
        Template mapperXmlTemplate = gt.getTemplate("mapperXml.btl");

        Entity entity = new Entity();
        entity.setEntityPackage(ENTITY_PACKAGE);
        entity.setDaoPackage(DAO_PACKAGE);
        entity.setServicePackage(SERVICE_PACKAGE);
        entity.setServiceImplPackage(SERVICE_IMPL_PACKAGE);
        entity.setControllerPackage(CONTROLLER_PACKAGE);
        entity.setAuthor(AUTHOR);
        entity.setClassName(isFirstUpper(CLASS_NAME, true));
        entity.setTableName(TABLE_PRE + camel2Underline(CLASS_NAME));
        entity.setClassNameLowerCase(isFirstUpper(CLASS_NAME, false));
        entity.setDescription(DESCRIPTION);
        entity.setPrimaryKeyType(PRIMARY_KEY_TYPE);
        entity.setCommonFiled(COMMON_Filed);

        // 生成实体类代码
        final String varEntity = "entity";
        entityTemplate.binding(varEntity, entity);
        String entityResult = entityTemplate.render();
        log.info(entityResult);
        // 创建文件
        String entityFileUrl = SYS_PATH + dotToLine(ENTITY_PACKAGE) + "/" + isFirstUpper(CLASS_NAME, true) + ".java";
        generateFile(entityTemplate, entityFileUrl);

        // 生成dao代码
        daoTemplate.binding(varEntity, entity);
        String daoResult = daoTemplate.render();
        log.info(daoResult);
        // 创建文件
        String daoFileUrl = SYS_PATH + dotToLine(DAO_PACKAGE) + "/" + isFirstUpper(CLASS_NAME, true) + "Mapper.java";
        generateFile(daoTemplate, daoFileUrl);

        // 生成service代码
        serviceTemplate.binding(varEntity, entity);
        String serviceResult = serviceTemplate.render();
        log.info(serviceResult);
        // 创建文件
        String serviceFileUrl = SYS_PATH + dotToLine(SERVICE_PACKAGE) + "/" + isFirstUpper(CLASS_NAME, true) + "Service.java";
        generateFile(serviceTemplate, serviceFileUrl);

        // 生成serviceImpl代码
        serviceImplTemplate.binding(varEntity, entity);
        String serviceImplResult = serviceImplTemplate.render();
        log.info(serviceImplResult);
        // 创建文件
        String serviceImplFileUrl = SYS_PATH + dotToLine(SERVICE_IMPL_PACKAGE) + "/" + isFirstUpper(CLASS_NAME, true) + "ServiceImpl.java";
        generateFile(serviceImplTemplate, serviceImplFileUrl);

        // 生成controller代码
        controllerTemplate.binding(varEntity, entity);
        String controllerResult = controllerTemplate.render();
        log.info(controllerResult);
        // 创建文件
        String controllerFileUrl = SYS_PATH + dotToLine(CONTROLLER_PACKAGE) + "/" + isFirstUpper(CLASS_NAME, true) + "Controller.java";
        generateFile(controllerTemplate, controllerFileUrl);

        // 生成mapperXml代码
        mapperXmlTemplate.binding(varEntity, entity);
        String mapperXmlResult = mapperXmlTemplate.render();
        log.info(mapperXmlResult);
        // 创建文件
        String mapperXmlFileUrl = BaseConstant.PROJECT_ROOT_DIRECTORY + "/src/main/resources/mapper/" + isFirstUpper(CLASS_NAME, true) + "Mapper.xml";
        generateFile(mapperXmlTemplate, mapperXmlFileUrl);
    }

    /**
     * 生成文件
     * @param entityTemplate 模板构造器
     * @param fileUrl 文件全路径
     */
    private static void generateFile(Template entityTemplate, String fileUrl) throws IOException {
        OutputStream out = null;
        File newFile = new File(fileUrl);
        File fileUrlDir = newFile.getParentFile();
        if (!fileUrlDir.exists()) {
            fileUrlDir.mkdirs();
        }
        if (!newFile.exists() && newFile.createNewFile()) {
            // 若文件存在则不重新生成
            out = new FileOutputStream(newFile);
            entityTemplate.renderTo(out);
        }

        if (out != null) {
            out.close();
        }
        log.info("生成代码成功！");
    }

    /**
     * 删除指定类代码
     * @param className
     * @throws IOException
     */
    @SneakyThrows
    public static void deleteCode(String className) {

        String entityFileUrl = SYS_PATH + dotToLine(ENTITY_PACKAGE) + "/" + isFirstUpper(className, true) + ".java";
        Files.delete(Paths.get(entityFileUrl));

        String daoFileUrl = SYS_PATH + dotToLine(DAO_PACKAGE) + "/" + isFirstUpper(className, true) + "Mapper.java";
        Files.delete(Paths.get(daoFileUrl));

        String serviceFileUrl = SYS_PATH + dotToLine(SERVICE_PACKAGE) + "/" + isFirstUpper(className, true) + "Service.java";
        Files.delete(Paths.get(serviceFileUrl));

        String serviceImplFileUrl = SYS_PATH + dotToLine(SERVICE_IMPL_PACKAGE) + "/" + isFirstUpper(className, true) + "ServiceImpl.java";
        Files.delete(Paths.get(serviceImplFileUrl));

        String controllerFileUrl = SYS_PATH + dotToLine(CONTROLLER_PACKAGE) + "/" + isFirstUpper(className, true) + "Controller.java";
        Files.delete(Paths.get(controllerFileUrl));

        String mapperXmlFileUrl = BaseConstant.PROJECT_ROOT_DIRECTORY + "/src/main/resources/mapper/" + isFirstUpper(className, true) + "Mapper.xml";
        Files.delete(Paths.get(mapperXmlFileUrl));

        log.info("删除代码完毕！");
    }

    private static void generatePlus(GroupTemplate gt) {

        try {
            generateMPlus(gt);
        } catch (Exception e) {
            log.info("请确保实体类存在并且已完善填入字段后再生成条件构造代码哦！");
        }
    }

    /** 生成查询代码 */
    @SneakyThrows
    private static void generateMPlus(GroupTemplate gt) {

        Template plusTemplate = gt.getTemplate("mplus.btl");

        Entity entity = new Entity();

        entity.setClassName(isFirstUpper(CLASS_NAME, true));
        entity.setClassNameLowerCase(isFirstUpper(CLASS_NAME, false));
        List<Item> items = new ArrayList<>();

        String path = ENTITY_PACKAGE + "." + isFirstUpper(CLASS_NAME, true);
        Class<Object> c = (Class<Object>) Class.forName(path);
        Object obj = c.getDeclaredConstructor().newInstance();
        java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {

            java.lang.reflect.Field field = fields[i];
            field.setAccessible(true);
            // 字段名
            String fieldName = field.getName();
            String fieldType = field.getType().getName();
            // 白名单
            if ("serialVersionUID".equals(fieldName)) {
                continue;
            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && !tableField.exist()) {
                continue;
            }

            // 获得字段注解
            ApiModelProperty myFieldAnnotation = field.getAnnotation(ApiModelProperty.class);
            String fieldNameCN = fieldName;
            if (myFieldAnnotation != null) {
                fieldNameCN = myFieldAnnotation.value();
            }
            fieldNameCN = StrUtil.isBlank(fieldNameCN) ? fieldName : fieldNameCN;

            if (fieldType.startsWith("java.lang.")) {
                fieldType = StrUtil.subAfter(fieldType, "java.lang.", false);
            }

            Item item = new Item();
            item.setType(fieldType);
            item.setUpperName(isFirstUpper(fieldName, true));
            item.setLowerName(isFirstUpper(fieldName, false));
            item.setLineName(camel2Underline(fieldName));
            item.setTitle(fieldNameCN);

            items.add(item);
        }

        // 绑定参数
        plusTemplate.binding("entity", entity);
        plusTemplate.binding("items", items);
        String result = plusTemplate.render();

        System.out.println("=================================================================================");
        System.out.println("=====生成条件构造代码Plus成功！请根据需要自行复制添加以下代码至控制层方法Controller中======");
        System.out.println("=================================条件构造代码起始线=================================");

        System.out.println(result);

        System.out.println("=================================条件构造代码终止线=================================");
        System.out.println("[代码方法添加位置：" + CONTROLLER_PACKAGE + "." + CLASS_NAME + "Controller.java]");
    }


    /**
     * 点转斜线
     * @param str
     * @return
     */
    public static String dotToLine(String str) {
        return str.replace(".", "/");
    }

    /**
     * 驼峰法转下划线
     */
    public static String camel2Underline(String str) {

        if (StrUtil.isBlank(str)) {
            return "";
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                sb.append("_" + Character.toLowerCase(str.charAt(i)));
            } else {
                sb.append(str.charAt(i));
            }
        }
        return (str.charAt(0) + sb.toString()).toLowerCase();
    }

    /**
     * 首字母是否大小写
     * @param name
     * @param isFirstUpper (true大写，false小写)
     * @return
     */
    public static String isFirstUpper(String name, boolean isFirstUpper) {

        if (StrUtil.isBlank(name)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "name不能为空");
        }

        if (name.length() == 1) {
            if (isFirstUpper) {
                return name.toUpperCase();
            } else {
                return name.toLowerCase();
            }
        }

        StringBuilder sb = new StringBuilder();
        if (isFirstUpper) {
            sb.append(Character.toUpperCase(name.charAt(0)));
        } else {
            sb.append(Character.toLowerCase(name.charAt(0)));
        }
        sb.append(name.substring(1));
        return sb.toString();
    }

    /**
     * 单例实现模板生成器
     */
    public static synchronized GroupTemplate getGroupTemplate() throws IOException {
        if (gt == null) {
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/btl/");
            Configuration cfg = Configuration.defaultConfiguration();
            gt = new GroupTemplate(resourceLoader, cfg);
        }
        return gt;
    }
}
