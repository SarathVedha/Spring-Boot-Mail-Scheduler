package com.vedha.mail;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Mail API", version = "1.0", description = "Mail API"))
public class SpringBootMailApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMailApplication.class, args);
    }

}
