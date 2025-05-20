package com.academy.apicrud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Reactiva de Gestión Médica")
                        .description("API REST reactiva desarrollada con Spring WebFlux, R2DBC y Project Reactor")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Alan Isaias Cairampoma Carrillo")
                                .email("alancairampoma@gmail.com")
                                .url("https://www.linkedin.com/in/alancairampomacarrillo/"))
                        .license(new License()
                                .name("Licencia API")
                                .url("https://www.example.com/license")));
    }
}