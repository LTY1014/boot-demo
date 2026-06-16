# boot-demo

<p align=center>
    <a href="http://gitee.com/liang-tian-yu">Spring Boot案例</a>
</p>
<p align="center">
<a target="_blank" href="http://gitee.com/liang-tian-yu">
    <img src="https://img.shields.io/badge/JDK-1.8+-green" ></img>
    <img src="https://img.shields.io/badge/springboot-2.7.0-green" ></img>
    <img src="https://img.shields.io/badge/mysql-5.7+-blue" ></img>
    <img src="https://img.shields.io/badge/MybatisPlus-3.5.1-green" ></img>
    <img src="https://img.shields.io/badge/Knife4j -3.0.3-brightgreen" ></img>
</a></p>


记录SpringBoot的demo用例

[TOC]

## 技术栈总览

| 分类 | 依赖 |
|------|------|
| 框架 | Spring Boot 2.7.0 / Spring Data JPA / Spring Web / Spring AOP |
| 数据库 | MySQL 8.0 / MyBatis Plus 3.5.1 / HikariCP |
| 工具库 | Hutool 5.8.9 / Commons Lang3 3.6 / Gson 2.8.2 |
| 文档 | Knife4j 3.0.3 (Swagger) |
| Excel | EasyExcel 3.1.0 / Apache POI |
| 报表 | JasperReports 6.17.0 |
| 代码生成 | Beetl 2.9.10 / Screw (数据库文档) |
| Web | Jsoup 1.15.3 (网页爬取) / HttpClient 4.5.13 |
| 安全 | Jasypt 3.0.4 (配置加密) |
| 邮件 | Spring Boot Mail Starter |
| IP | IP2Region 2.7.0 |
| SQL解析 | JSqlParser 4.2 / OpenAPI Parser 1.0.7 |
| 规则引擎 | QLExpress 3.3.3 |
| 其他 | Lombok / JOL (内存分析) |



## 项目配置

### 启动配置

- 服务端口：`8088`
- 应用名：`boot-demo`
- Session超时：86400秒（1天）
- Jackson时间格式：`yyyy-MM-dd HH:mm:ss`，时区 GMT+8

### 数据库连接池（HikariCP）

```yaml
hikari:
  minimum-idle: 10        # 最小空闲连接
  maximum-pool-size: 50   # 最大连接数
  connection-timeout: 30000
  idle-timeout: 600000
  max-lifetime: 1800000
  leak-detection-threshold: 60000
```

### JPA 配置

```yaml
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
```

### MyBatis Plus 配置

```yaml
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  global-config:
    db-config:
      logic-delete-field: isDelete
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 多环境打包

```bash
mvn clean package -Pdev   # 开发环境（默认）
mvn clean package -Pprod  # 生产环境
```

---

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
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
```

- 实体类

 `@Entity` // 作为 hibernate实体类
 `@Table(name = "tb_name")` // 配置数据库表的名称,实体类中属性和表中字段的映射关系

- 具体测试看JpaTest



## Knife4j（接口文档）

配置详见 `Knife4jConfig`

application.yml

```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER

knife4j:
  enable: true
```

访问地址：`http://localhost:8088/doc.html`



## MyBatisPlus

### 自定义主键策略

- 定义主键策略

```java
public class CustomIdGenerator implements IdentifierGenerator {
    @Override
    public Long nextId(Object entity) {
        String serialId = SerialUtil.generateSerial();
        return Long.valueOf(serialId);
    }
}
```

- 注入

```java
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

```java
@TableId(type = IdType.ASSIGN_ID, value = "id")
private String id;
```



## 代码生成器

使用 Beetl 模板生成 Entity、Mapper、Service、Controller。

运行 `MybatisGenerate.java` 的 main 方法即可，详见代码注释。

SQL生成器 `SqlGenerate.java` 可根据建表SQL生成实体类字段信息（字段名、Java类型、注释）。



## 认证与权限

### @AuthCheck 注解

```java
@AuthCheck(anyRole = {"admin", "test", "user"})  // 拥有任意一个角色即可访问
@AuthCheck(mustRole = "admin")                   // 必须拥有指定角色才可访问
```

配合 `AuthInterceptor` 拦截器实现接口级别的权限控制。

### 登录与注销

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/register` | POST | 用户注册 |
| `/user/login` | POST | 用户登录（设置Session） |
| `/user/logout` | POST | 用户注销 |
| `/user/current` | GET | 获取当前登录用户 |
| `/user/update` | POST | 更新用户信息 |
| `/user/updatePassword` | POST | 修改密码 |



## 验证码（CAPTCHA）

使用 Hutool 的 `LineCaptcha` 生成图片验证码。

| 接口 | 方法 | 说明 |
|------|------|------|
| `/captcha/generate` | GET | 生成验证码图片（存入Session） |
| `/captcha/verify` | GET | 验证验证码是否正确 |

```java
// 生成验证码（宽120，高40，4位字符，50条干扰线）
LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 50);
request.getSession().setAttribute("captcha", captcha.getCode());
captcha.write(response.getOutputStream());
```



## Excel 导入导出

### POI 原生方式

| 接口 | 方法 | 说明 |
|------|------|------|
| `/excel/export` | POST | POI导出Excel |
| `/excel/import` | POST | POI导入Excel |
| `/excel/import2` | POST | POI迭代器方式导入 |
| `/excel/template` | POST | 导出Excel模板 |

工具类 `ExcelUtil`：写入数据、支持指定列红色字体、通过反射映射字段。

### EasyExcel 方式

| 接口 | 方法 | 说明 |
|------|------|------|
| `/excel/easyexcel/export` | POST | EasyExcel导出 |
| `/excel/easyexcel/import` | POST | EasyExcel导入 |
| `/excel/easyexcel/util/export` | POST | ExcelWriteUtil导出 |

工具类：`ExcelListener`（监听器）、`ExcelDataValidator`（数据校验）、`ExcelWriteUtil`（写入工具）。



## 邮件服务

`MailUtil` 支持三种邮件发送方式：

```java
// 文本邮件
mailUtil.sendSimpleMail(to, subject, content);

// HTML邮件
mailUtil.sendHtmlMail(to, subject, htmlContent);

// 带附件邮件
mailUtil.sendAttachmentMail(to, subject, content, files);
mailUtil.sendAttachmentMail(to, subject, content, bytes, fileName);
```

配置（application.yml）：

```yaml
spring.mail:
  host: smtp.163.com
  port: 465
  username: xxx@163.com
  password: xxx
  properties:
    mail.smtp.ssl.enable: true
```

使用 `@ConditionalOnProperty` 控制，`spring.mail.username` 未配置时不加载该Bean。



## IP 地址查询

`IpInfoUtil`：从请求头中获取真实IP（支持代理），并通过 IP2Region 查询地理位置。

```java
// 获取客户端真实IP（支持多层代理）
String ip = IpInfoUtil.getIpAddress(request);

// 根据IP查询地理位置（需要 static/ip2region.xdb 文件）
String region = IpInfoUtil.getClient("8.8.8.8");
```



## 设备信息识别

使用 Hutool 的 `UserAgentUtil` 解析 User-Agent：

```java
UserAgent ua = UserAgentUtil.parse(request.getHeader("user-agent"));
String device = ua.getBrowser() + " | " + ua.getPlatform() + " | " + (ua.isMobile() ? "移动端" : "PC端");
```



## JCIFS

- 导入依赖

```plain
        <!-- jcifs 访问SMB共享文件夹 -->
        <dependency>
            <groupId>jcifs</groupId>
            <artifactId>jcifs</artifactId>
            <version>1.3.17</version>
            <scope>compile</scope>
        </dependency>
```





- 工具类

```plain
package com.lty.util;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * jcifs SMB工具类
 */
public class JcifsSmbUtil {

    /**
     * 获取认证对象
     * @param domain 工作组填空字符串""，域环境填域名
     * @param username Windows登录账号
     * @param password Windows密码
     * @return NtlmPasswordAuthentication
     */
    public static NtlmPasswordAuthentication getAuth(String domain, String username, String password) {
        return new NtlmPasswordAuthentication(domain, username, password);
    }

    /**
     * 遍历共享文件夹下所有文件
     * @param smbUrl 共享路径格式：smb://192.168.1.100/共享文件夹名/
     * @param auth 账号密码凭证
     * @throws IOException
     */
    public static void listSmbDir(String smbUrl, NtlmPasswordAuthentication auth) throws IOException {
        SmbFile smbDir = new SmbFile(smbUrl, auth);
        if (!smbDir.exists()) {
            System.out.println("共享目录不存在：" + smbUrl);
            return;
        }
        if (!smbDir.isDirectory()) {
            System.out.println("路径不是文件夹");
            return;
        }
        SmbFile[] files = smbDir.listFiles();
        for (SmbFile file : files) {
            System.out.printf("文件名：%s，大小：%d byte%n", file.getName(), file.length());
        }
    }

    /**
     * 获取共享文件输入流，直接读取文件内容
     * @param smbFileUrl smb完整文件路径
     * @param auth 认证凭证
     * @return 文件输入流
     * @throws IOException
     */
    public static InputStream getFileInputStream(String smbFileUrl, NtlmPasswordAuthentication auth) throws IOException {
        SmbFile smbFile = new SmbFile(smbFileUrl, auth);
        if (!smbFile.exists() || smbFile.isDirectory()) {
            throw new IOException("目标不存在或为文件夹：" + smbFileUrl);
        }
        return new SmbFileInputStream(smbFile);
    }

    /**
     * 下载共享文件到本地磁盘
     * @param smbFilePath smb文件地址
     * @param localSavePath 本地保存路径
     * @param auth 认证凭证
     * @throws IOException
     */
    public static void downloadFileToLocal(String smbFilePath, String localSavePath, NtlmPasswordAuthentication auth) throws IOException {
        try (InputStream in = getFileInputStream(smbFilePath, auth);
             FileOutputStream out = new FileOutputStream(localSavePath)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
    }

    // 测试示例
    public static void main(String[] args) {
        // 服务端IP
        String ip = "192.168.1.100";
        // 工作组电脑domain填空，域环境填写域名
        String domain = "";
        String user = "Administrator";
        String pwd = "123456";
        // 共享文件夹路径
        String sharePath = "smb://" + ip + "/共享文件/";
        // 需要读取的文件
        String targetFile = "smb://" + ip + "/共享文件/test.txt";

        try {
            NtlmPasswordAuthentication auth = getAuth(domain, user, pwd);
            // 1. 遍历目录
            listSmbDir(sharePath, auth);
            // 2. 下载文件到本地
            downloadFileToLocal(targetFile, "D:/test.txt", auth);
            System.out.println("文件下载完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



**扩展：SpringBoot 项目封装工具**

可将`CIFSContext`做成 Bean，全局复用连接，避免重复认证：

```plain
@Bean
public CIFSContext smbContext() {
    return SmbFileUtil.getSmbContext("192.168.1.100", "", "admin", "123456");
}
```



**权限配置要点（Windows 服务器）**

1. 共享文件夹右键【共享】添加账号，赋予读取权限
2. 安全选项卡添加同一账号，文件系统读取权限
3. 关闭防火墙或放行 445 端口
4. 来宾账户禁用时，必须使用有效 Windows 账号登录
5. 仅支持 SMB1 协议(Windows 新版系统默认关闭 SMB1，会报连接失败)
6. 

## 安全工具类

### PasswordUtil（密码加密）

```java
// MD5加密（加盐）
String hashed = PasswordUtil.encodePassword("123456");

// 验证密码
boolean valid = PasswordUtil.isValidPassword(input, hashed);

// 检查密码强度（大小写+数字+特殊字符）
boolean strong = PasswordUtil.isPasswordStrong(password);

// 生成随机密码/盐值
String randomPwd = PasswordUtil.generateRandomPassword(16);
String salt = PasswordUtil.generateSalt();
```

### RsaUtil（RSA非对称加密）

```java
// 生成密钥对
RsaKeyPair keyPair = RsaUtil.generateKeyPair();

// 公钥加密 / 私钥解密
String encrypted = RsaUtil.encryptByPublicKey(publicKey, text);
String decrypted = RsaUtil.decryptByPrivateKey(privateKey, encrypted);

// 私钥加密 / 公钥解密
String encrypted2 = RsaUtil.encryptByPrivateKey(privateKey, text);
String decrypted2 = RsaUtil.decryptByPublicKey(publicKey, encrypted2);
```

### SignatureUtil（HMAC-SHA256签名）

```java
// 生成随机密钥（16位字母）
String secretKey = SignatureUtil.generateRandomKey();

// 生成签名
String signature = SignatureUtil.generateSignature(data, secretKey);

// 验证签名
boolean valid = SignatureUtil.verifySignature(data, signature, secretKey);
```

### JasyptUtil（配置文件加密）

```yaml
jasypt:
  encryptor:
    password: lty
```

在配置文件中使用 `ENC(密文)` 格式加密敏感信息：

```yaml
password: ENC(fs0jGHuO1DbOEgiWmeXLWperUh7H2pXHJmIdIHb5FRbz3sVnR9zXXmqknu04J4qe)
```

加密算法：`PBEWITHHMACSHA512ANDAES_256`



## SQL 校验工具

`SqlValidUtil` 基于 JSqlParser 实现：

```java
// 校验SQL语法
boolean valid = SqlValidUtil.validSql("SELECT * FROM user WHERE id = 1");

// 校验表名（正则匹配）
boolean valid = SqlValidUtil.validSqlTableName(sql, "ittr$");

// 校验必须包含的列
boolean valid = SqlValidUtil.validSqlColumn(sql, List.of("id", "name"));

// 获取数据库名和表名
List<String> schemaTable = SqlValidUtil.getSchemaTable(sql);  // [数据库, 表]
```



## 唯一ID生成

### SnowFlake（雪花算法）

```java
Long id = SnowFlakeUtil.nextId();  // 生成19位ID
```

### SerialUtil（日期序列号）

```java
String serial = SerialUtil.generateSerial();  // 如：202603301234
```

格式：`yyyyMMdd` + 4位当日流水号（自动补零、线程安全）。



## 树形结构工具

`TreeUtil` 支持：

- **构建树**：`makeTree(list, getPid, getId, rootCheck, setChildren)`
- **树中过滤**（自上而下，保留满足条件的节点及其子树）：`filter(tree, predicate, getChildren)`
- **树中搜索**（自下而上，保留满足条件的节点及其所有祖先路径）：`search(tree, predicate, getChildren)`

```java
List<Menu> tree = TreeUtil.makeTree(
    list,
    Menu::getParentId,
    Menu::getId,
    node -> node.getParentId() == 0,
    Menu::setChildren
);
```



## QLExpress 规则引擎

通过配置 `ExpressRunner` 和 `DefaultContext`，支持表达式执行：

```java
// 基础运算
Object result = qlExpressService.executeExpression("(a + b) * c", params);

// 条件判断
Object result = qlExpressService.executeExpression("if (a>b) then {return a;} else {return b;}", params);

// 业务规则（折扣计算）
params.put("amount", new BigDecimal("5000"));
params.put("productType", "ELECTRONICS");
params.put("vipLevel", 5);

for (String rule : getRules()) {
    Object result = qlExpressService.executeExpression(rule, params);
}
```



## OpenAPI 文档解析

`ApiInfoUtil` 基于 `openapi-parser` 解析远程 Swagger 文档：

```java
ApiInfoDTO dto = new ApiInfoDTO();
dto.setApiUrl("http://localhost:8088/api/v3/api-docs");
dto.setBaseUrl("/api");
List<ApiInfo> apiInfoList = ApiInfoUtil.getApiInfoList(dto);
```

提取接口路径、HTTP方法、接口名、描述等信息。



## 命令执行

`CommandUtil` 执行系统命令/脚本（支持超时控制60秒）：

```java
// 执行Shell脚本（Linux/Mac）
String result = CommandUtil.executeCommand(List.of("/bin/bash", "/opt/test.sh"), "/workDir");

// 执行系统命令
String result = CommandUtil.executeCommand(List.of("ls", "-l"));

// 执行批处理脚本（Windows）
String result = CommandUtil.executeCommand(List.of("cmd", "/c", "D:\\test.bat"));
```



## 报表生成（JasperReports）

`ReportTest` 通过 `.jrxml` 模板生成 PDF 报表：

```java
JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate);

// 方式一：数据库连接填充
JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

// 方式二：JavaBean集合填充
JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

// 导出PDF
JasperExportManager.exportReportToPdfFile(jasperPrint, "report.pdf");
```



## 数据库文档生成（Screw）

`ScrewTest` 根据数据库表结构生成 Markdown 格式的数据库设计文档：

```java
EngineConfig engineConfig = EngineConfig.builder()
    .fileOutputDir("D:\\Desktop\\screw")
    .openOutputDir(false)
    .fileType(EngineFileType.MD)
    .produceType(EngineTemplateType.freemarker).build();

Configuration config = Configuration.builder()
    .version("1.0.0")
    .description("数据库设计文档")
    .dataSource(hikariDataSource)
    .engineConfig(engineConfig)
    .produceConfig(getProcessConfig()).build();

new DocumentationExecute(config).execute();
```

支持按表名、前缀、后缀过滤，可生成 HTML/WORD/MD 三种格式。

`getProcessConfig()` 方法可配置忽略的表名、表前缀、表后缀。



## 网页爬取（Jsoup）

`JsoupTest` 抓取网页内容：

```java
Document document = Jsoup.connect(url).get();
Elements title = document.getElementsByClass("article-title");
Elements content = document.getElementsByClass("article-viewer markdown-body result");
```



## HTTP 客户端

### HttpClientUtil

封装了 GET/POST 请求，支持 Cookie 持久化：

```java
BaseResponse response = HttpClientUtil.getRequest("http://api.example.com/data");
BaseResponse response = HttpClientUtil.postRequest("http://api.example.com/login", requestBody);
HttpClientUtil.addCookie("session", "abc123", "example.com");
HttpClientUtil.clearCookies();
```

### RestTemplate

支持普通请求、带Cookie请求、对象映射：

```java
// GET
ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

// POST (JSON)
HttpEntity<String> request = new HttpEntity<>(json, headers);
ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

// POST (对象，自动序列化)
HttpEntity<UserLoginRequest> request = new HttpEntity<>(requestBody, headers);
ResponseEntity<BaseResponse> response = restTemplate.postForEntity(url, request, BaseResponse.class);

// 带Cookie的请求（会话保持）
CloseableHttpClient httpClient = HttpClients.custom()
    .setDefaultCookieStore(new BasicCookieStore()).build();
HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
RestTemplate restTemplate = new RestTemplate(factory);
```



## JSON 处理

### GsonUtil

```java
// Bean转JSON
String json = GsonUtil.beanToJson(user);

// JSON转Bean
User user = GsonUtil.jsonToBean(json, User.class);

// JSON转List
List<User> list = GsonUtil.jsonToList(json, User.class);

// List转JSON
String json = GsonUtil.listToJson(list);
```

支持 `LocalDateTime` 类型自动序列化/反序列化（格式 `yyyy-MM-dd HH:mm:ss`）。

### Hutool JSON

```java
// 对象转JSON字符串
String json = JSONUtil.toJsonStr(obj);

// 解析数组
JSONArray jsonArray = JSONUtil.parseArray(json);

// 解析对象
JSONObject jsonObject = JSONUtil.parseObj(obj);
```



## 异步任务（CompletableFuture）

```java
// 异步任务
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "async result";
});

// 合并结果
future.thenCombine(otherFuture, (r1, r2) -> r1 + r2);

// 依赖执行
future.thenCompose(result -> CompletableFuture.supplyAsync(() -> result * 2));

// 异常处理
future.exceptionally(ex -> {
    return "default value";
});
```

配合 `AsyncConfig`（核心线程10，最大30，队列2000），通过 `@Async` 注解启用异步执行。

线程池配置类实现了 `AsyncConfigurer` 接口，接管 Spring 默认的异步异常处理。



## 请求日志拦截（AOP）

`LogInterceptor` 使用 `@Around` 切面拦截所有 `com.lty.controller.*` 下的方法：

- 记录请求ID、路径、IP、参数
- 记录方法执行耗时
- 使用 `StopWatch` 精确计时

```java
log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url, ip, reqParam);
log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
```



## 全局异常处理

`GlobalExceptionHandler` 统一处理：

- `BusinessException`：业务异常（自定义错误码）
- `RuntimeException`：运行时异常

`ErrorCode` 定义了项目级错误码：

| 错误码 | 说明 |
|--------|------|
| 0 | SUCCESS |
| 40000 | PARAMS_ERROR |
| 40100 | NOT_LOGIN_ERROR |
| 40101 | NO_AUTH_ERROR |
| 40400 | NOT_FOUND_ERROR |
| 40300 | FORBIDDEN_ERROR |
| 42900 | TOO_MANY_REQUEST |
| 50000 | SYSTEM_ERROR |
| 50001 | OPERATION_ERROR |

`ErrorCodePageHandler` 配置了错误页面映射（404.html / 500.html）。



## 跨域与拦截器

`CorsConfig` 配置：

- 全局跨域（支持 Cookie、允许所有来源）
- 注册 `PathInterceptor`（拦截 `/demo` 路径）
- 静态资源映射（支持 Knife4j 文档资源）
- 视图控制器

```java
registry.addMapping("/**")
    .allowCredentials(true)
    .allowedOriginPatterns("*")
    .allowedMethods("*")
    .allowedHeaders("*")
    .exposedHeaders("*");
```



## 启动与关闭钩子

`AppTask` 实现 `ApplicationRunner` 和 `ClosedAware`：

- 启动时打印应用信息（端口、运行目录、JDK版本等）
- 关闭时执行清理逻辑

```java
@Component
public class AppTask implements ApplicationRunner, DisposableBean {
    @Override
    public void run(ApplicationArguments args) { /* 启动后执行 */ }
}
```



## 多系统 Webhook 同步方案

(主流程同步保证数据一致性，通知逻辑异步提升性能)



- 测试方法

```
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebhookTest {
    @Autowired
    private BizDataService bizDataService;

    @Test
    void testDataUpdateSync() {
        // 调用业务更新 → 自动触发 B/C 系统同步
        bizDataService.updateData(1001L, "测试业务数据", 1);
    }
}
```



## 其他工具类

| 工具类 | 功能 |
|--------|------|
| `BaseUtil` | 基础工具方法 |
| `GrammarUtil` | SQL类型转Java类型映射 |
| `ConversionUtil` | 类型转换工具 |
| `MappingUtil` | 对象映射工具 |
| `RatioUtil` | 比例计算工具 |
| `RecursiveUtil` | 递归工具 |
| `ValidateUtil` | 校验工具 |
| `ServletUtil` | Servlet上下文获取、JSON响应输出 |
| `LocalDateTimeUtil` | LocalDateTime 操作工具 |
| `BusinessFileUtil` | 业务文件处理 |
| `SpringUtil` | Spring上下文获取 |
| `SysCacheUtil` | 系统缓存工具 |



## 测试用例

| 测试类 | 覆盖内容 |
|--------|----------|
| `AppTest` | Book 增删改查 |
| `EasyExcelTest` | EasyExcel 导入导出 |
| `HuToolTest` | Hutool HTTP请求、JSON操作、系统信息 |
| `Lang3Test` | Commons Lang3 字符串/系统/枚举工具 |
| `GsonTest` | Gson 序列化/反序列化 |
| `JsqlparserTest` | SQL解析、建表SQL转实体、INSERT合并 |
| `ReportTest` | JasperReports PDF报表生成 |
| `ScrewTest` | 数据库文档生成 |
| `JsoupTest` | 网页爬取 |
| `ApiPathTest` | OpenAPI 文档解析 |
| `MailTest` | 文本/HTML/附件邮件发送 |
| `QLExpressTest` | 规则引擎表达式执行、折扣计算 |
| `RestTemplateTest` | GET/POST请求、Cookie会话保持 |
| `HttpTest` | HttpClient 原生用法 |
| `CompletableFutureTest` | 异步任务、合并、异常处理 |
| `BigDecimalTest` | 精确计算、舍入、精度控制 |
| `JolTest` | JVM 对象内存占用分析 |
| `BeetlTest` | Beetl 模板引擎测试 |
| `ReflectionTest` | 反射工具测试 |
