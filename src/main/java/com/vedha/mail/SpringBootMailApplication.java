package com.vedha.mail;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Mail API", version = "1.0", description = "Mail API"))
public class SpringBootMailApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringBootMailApplication.class, args);

        // Command line args: java -jar target/spring-boot-mail-0.0.1-SNAPSHOT.jar arg1 arg2
        log.info("Command line args: {} ", Arrays.toString(args)); // [arg1, arg2]
    }

}
