package ru.api.moviepark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class MovieParkApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MovieParkApplication.class);
        application.run(args);
    }
}
