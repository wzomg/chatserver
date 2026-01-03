package com.zzw.chatserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI chatServerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("聊天系统API文档")
                        .description("本文档描述了聊天系统接口定义")
                        .version("1.0")
                        .contact(new Contact()
                                .name("java")
                                .url("http://baidu.com")
                                .email("sharezzw@163.com")));
    }
}
