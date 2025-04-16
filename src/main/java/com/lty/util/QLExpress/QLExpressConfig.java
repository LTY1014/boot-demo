package com.lty.util.QLExpress;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QLExpressConfig {

    @Bean
    public ExpressRunner expressRunner() {
        return new ExpressRunner();
    }

    @Bean
    public DefaultContext<String, Object> defaultContext() {
        return new DefaultContext<>();
    }
}