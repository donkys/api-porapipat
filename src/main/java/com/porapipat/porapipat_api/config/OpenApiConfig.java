package com.porapipat.porapipat_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Porapipat API")
                        .version("1.0.0")
                        .description("This is a sample API for demonstration purposes.")
                        .termsOfService("http://porapipat.me/terms/")
                        .contact(new Contact()
                                .name("Porapipat Support")
                                .url("http://porapipat.me.com/contact")
                                .email("k.porapipat@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}