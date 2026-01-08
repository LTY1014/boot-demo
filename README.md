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



application.yml

```
# 解决swagger和springBoot高版本冲突问题
spring:
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
```





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





[油猴脚本](https://juejin.cn/post/7517081861975277603)



- websocket
- spring-security
- boot-test
- antdesign  tree树使用



## TreeUtil

```plain
package com.lty.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Description: 树操作方法工具类
 * @author lty
 */
public class TreeUtil {
    /**
     * 使用Map合成树
     *
     * @param menuList       需要合成树的List
     * @param pId            对象中的父ID字段,如:Menu:getPid
     * @param id             对象中的id字段 ,如：Menu:getId
     * @param rootCheck      判断E中为根节点的条件，如：x->x.getPId()==-1L , x->x.getParentId()==null,x->x.getParentMenuId()==0
     * @param setSubChildren E中设置下级数据方法，如： Menu::setSubMenus
     * @param <T>            ID字段类型
     * @param <E>            泛型实体对象
     * @return
     */
    public static <T, E> List<E> makeTree(List<E> menuList, Function<E, T> pId, Function<E, T> id, Predicate<E> rootCheck, BiConsumer<E, List<E>> setSubChildren) {
        // 按原数组顺序构建父级数据Map，使用Optional考虑pId为null
        Map<Optional<T>, List<E>> parentMenuMap = menuList.stream().collect(Collectors.groupingBy(
                node -> Optional.ofNullable(pId.apply(node)),
                LinkedHashMap::new,
                Collectors.toList()
        ));
        List<E> result = new ArrayList<>();
        for (E node : menuList) {
            // 添加到下级数据中
            setSubChildren.accept(node, parentMenuMap.get(Optional.ofNullable(id.apply(node))));
            // 如里是根节点，加入结构
            if (rootCheck.test(node)) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 树中过滤
     *
     * @param tree        需要过滤的树
     * @param predicate   过滤条件
     * @param getChildren 获取下级数据方法，如：MenuVo::getSubMenus
     * @param <E>         泛型实体对象
     * @return List<E> 过滤后的树
     */
    public static <E> List<E> filter(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getChildren) {
        return tree.stream().filter(item -> {
            if (predicate.test(item)) {
                List<E> children = getChildren.apply(item);
                if (children != null && !children.isEmpty()) {
                    filter(children, predicate, getChildren);
                }
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }


    /**
     * 树中搜索
     *
     * @param tree
     * @param predicate
     * @param getSubChildren
     * @param <E>
     * @return 返回搜索到的节点及其父级到根节点
     */
    public static <E> List<E> search(List<E> tree, Predicate<E> predicate, Function<E, List<E>> getSubChildren) {
        Iterator<E> iterator = tree.iterator();
        while (iterator.hasNext()) {
            E item = iterator.next();
            List<E> childList = getSubChildren.apply(item);
            if (childList != null && !childList.isEmpty()) {
                search(childList, predicate, getSubChildren);
            }
            if (!predicate.test(item) && (childList == null || childList.isEmpty())) {
                iterator.remove();
            }
        }
        return tree;
    }
}
```
