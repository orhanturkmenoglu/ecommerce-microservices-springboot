package com.example.product_service.config;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(info()
                        .contact(contact()));
    }

    @Bean
    public Info info() {
        return new Info()
                .title("Microservice API Documentation")
                .version("1.0")
                .description("API documentation for the microservices.");
    }

    @Bean
    public Contact contact() {
        return new Contact()
                .name("Orhan")
                .email("orhantrkmn749@mail.com")
                .url("https://github.com/orhanturkmenoglu");
    }
}