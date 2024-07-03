package com.lty.service;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrewTest {

   public String[] dbNames = {"demo"};
   public String fileOutputDir = "D:\\Desktop\\screw";

   @Test
   void generate() {
      // 生成文件配置
      EngineConfig engineConfig = EngineConfig.builder()
            // 生成文件路径，这里需要自己更换下路径
            .fileOutputDir(fileOutputDir)
            // 打开目录
            .openOutputDir(false)
            // 文件类型 HTML/WORD/MD 三种格式
            .fileType(EngineFileType.MD)
            // 生成模板实现
            .produceType(EngineTemplateType.freemarker).build();

      //数据库名称（数据库用户连接）
      for (String dbName : dbNames) {
         HikariDataSource hikariDataSource = new HikariDataSource();
         //设置数据库连接
         hikariDataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/"+dbName+"?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true");
         hikariDataSource.setUsername("root");
         hikariDataSource.setPassword("123456");
         // 生成文档配置（包含以下自定义版本号、描述等配置连接）
         Configuration config = Configuration.builder()
                 .version("1.0.0")
                 .description("数据库设计文档")
                 .dataSource(hikariDataSource)
                 .engineConfig(engineConfig)
                 .produceConfig(getProcessConfig())
                 .build();

         // 执行生成
         new DocumentationExecute(config).execute();
      }

   }

   /**
    * 配置想要生成的表+ 配置想要忽略的表
    * @return 生成表配置
    */
   public static ProcessConfig getProcessConfig(){
      // 忽略表名
      List<String> ignoreTableName = Arrays.asList("testa_testa","testb_testb");
      // 忽略表前缀
      List<String> ignorePrefix = Arrays.asList("testa","testb");
      // 忽略表后缀
      List<String> ignoreSuffix = Arrays.asList("_testa","_testb");
      return ProcessConfig.builder()
            //根据名称指定表生成 我需要生成所有表 这里暂时不设置
            .designatedTableName(new ArrayList<>())
            //根据表前缀生成 我需要生成所有表 这里暂时不设置
            .designatedTablePrefix(new ArrayList<>())
            //根据表后缀生成 我需要生成所有表 这里暂时不设置
            .designatedTableSuffix(new ArrayList<>())
            //忽略表名
            .ignoreTableName(ignoreTableName)
            //忽略表前缀
            .ignoreTablePrefix(ignorePrefix)
            //忽略表后缀
            .ignoreTableSuffix(ignoreSuffix).build();
   }
}