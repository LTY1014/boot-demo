## boot-demo



<p align=center>
    <a href="http://gitee.com/liang-tian-yu">Spring Boot案例</a>
</p>
<p align="center">
<a target="_blank" href="http://gitee.com/liang-tian-yu">
    <img src="https://img.shields.io/badge/JDK-1.8+-green" ></img>
    <img src="https://img.shields.io/badge/springboot-2.7.0-green" ></img>
    <img src="https://img.shields.io/badge/mysql-8.0-blue" ></img>
    <img src="https://img.shields.io/badge/MybatisPlus-3.5.1-green" ></img>
    <img src="https://img.shields.io/badge/Knife4j -3.0.3-brightgreen" ></img>
</a></p>



记录SpringBoot的demo用例

[TOC]



## JPA

- 导入依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
```



- yml配置

```
spring:
  jpa:
    hibernate:
      ddl-auto: update
      naming:
        # 驼峰命名
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    show-sql: true
    # 默认引擎为InnoDB
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
```



- **实体类**

 @Entity // 作为 hibernate实体类
 @Table(name = "tb_name") // 配置数据库表的名称,实体类中属性和表中字段的映射关系



- 具体测试看JpaTest





## Knife4j

接口文档



配置详见`Knife4jConfig`



## MybaisPlus

自定义生成主键策略

- 定义主键策略

```
public class CustomIdGenerator implements IdentifierGenerator  {

    @Override
    public Long nextId(Object entity) {
        String serialId = SerialUtil.generateSerial();
        return Long.valueOf(serialId);
    }
}
```



- 注入

```
@Configuration
@MapperScan({"com.lty.mapper","com.lty.*.mapper"})
public class MybatisPlusConfig {

    //@Bean
    //public IdentifierGenerator identifierGenerator() {
    //    return new CustomIdGenerator();
    //}
}

```



- 注解使用

```
@TableId(type = IdType.ASSIGN_ID, value = "id")
private String id;
```
