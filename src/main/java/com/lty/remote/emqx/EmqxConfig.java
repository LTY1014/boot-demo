package com.lty.remote.emqx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.emqx")
public class EmqxConfig {

    private String url;

    private String username;

    private String password;
}
