package ru.api.moviepark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieParkApplication {
    public static void main(String[] args) {
        if (args.length == 3) {
            System.setProperty("spring.datasource.url", args[0]);
            System.setProperty("spring.datasource.username", args[1]);
            System.setProperty("spring.datasource.password", args[2]);
        }

        SpringApplication application = new SpringApplication(MovieParkApplication.class);
        application.setAddCommandLineProperties(false);
        application.run(args);
    }
}
