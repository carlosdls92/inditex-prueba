package es.com.inditex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ApiImplApp {
    public static void main(String[] args) {
        SpringApplication.run(ApiImplApp.class, args);
        log.info("http://localhost:8080/swagger-ui.html");
    }
}