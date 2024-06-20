package com.lty.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author lty
 */
@Slf4j
@Configuration
@EnableSwagger2
public class Knife4jConfig {

    @Bean
    public Docket webApiConfig() {

        log.info("Load knife4j ===========================");

        return new Docket(DocumentationType.OAS_30)
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lty.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API Document")
                .description("API文档")
                .termsOfServiceUrl("127.0.0.1")
                .version("1.0")
                .contact(new Contact("lty", "https://gitee.com/liang-tian-yu", "xxx@qq.com"))
                .build();
    }

}
