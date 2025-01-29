package com.lty.code;

import cn.hutool.core.util.StrUtil;
import com.lty.code.bean.Entity;
import com.lty.code.bean.Field;
import com.lty.common.ErrorCode;
import com.lty.constant.BaseConstant;
import com.lty.exception.BusinessException;
import com.lty.util.GrammarUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Arrays;
import java.util.List;

/**
 * 通过SQL实现代码生成
 * @author lty
 */
@Slf4j
public class SqlGenerate {

    private static GroupTemplate gt;

    public static void main(String[] args) throws Exception {
        GroupTemplate gt = getGroupTemplate();

        // 生成代码(补充到实体类字段)
        generateCode(gt);

    }

    public static String createTableSql = """
                 create table log
                (
                    id         varchar(255)                       not null comment 'id'
                        primary key,
                    ip         varchar(256)                       null comment 'ip地址',
                    path       varchar(256)                       null comment '请求路径',
                    params     varchar(512)                       null comment '请求参数',
                    cost       bigint                             null comment '请求耗时(ms)',
                    type       varchar(256)                       null comment '请求类型',
                    userId     bigint                             null comment '用户Id',
                    client     varchar(256)                       null comment '客户端',
                    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
                    isDelete   tinyint  default 0                 not null comment '是否删除'
                )
                    comment '日志';            
            """;

    //region 配置选项
    /** 忽略字段 */
    public static final List<String> ignoreField = new ArrayList<>(Arrays.asList("id"));

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

    public static List<Field> generateFieldFromCreateTable(String createTableSql) throws Exception {
        // 解析建表语句
        CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(createTableSql);
        // 字段定义
        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
        List<Field> fields = new ArrayList<>();
        for (ColumnDefinition column : columnDefinitions) {
            String columnName = column.getColumnName();
            // 跳过某些特殊字段
            if (ignoreField.contains(columnName)) {
                continue;
            }
            String columnComment = "";
            if (column.getColumnSpecs().size() <= 3) {
                columnComment = StringUtils.replace(column.getColumnSpecs().get(2), "'", "");
            }
            String dataType = column.getColDataType().getDataType();
            String javaType = GrammarUtil.mapSqlTypeToJavaType(dataType);

            Field field = new Field();
            field.setField(GrammarUtil.isFirstUpper(columnName, false));
            field.setComment(columnComment);
            field.setJavaType(javaType);
            fields.add(field);
        }

        // TODO 1.id问题  2.not null
        return fields;
    }

    /**
     * 生成代码
     * @param gt
     * @throws IOException
     */
    private static void generateCode(GroupTemplate gt) throws Exception {

        Template entitySqlTemplate = gt.getTemplate("entitySql.btl");

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
        List<Field> fields = generateFieldFromCreateTable(createTableSql);
        entitySqlTemplate.binding("entity", entity);
        entitySqlTemplate.binding("fields", fields);
        String entityResult = entitySqlTemplate.render();
        log.info(entityResult);
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
