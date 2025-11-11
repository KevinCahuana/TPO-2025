package br.com.lefranchi.hexagonal.demo.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
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
                        .title("Hexagonal Demo API")
                        .version("1.0.0")
                        .description("Demo project for Hexagonal Architecture with Spring Boot")
                        .license(new License()
                                .name("MIT License")
                                .url("https://github.com/lefranchi/hexagonal-architecture-demo/blob/main/LICENSE"))); // Puedes ajustar esta URL
    }
}