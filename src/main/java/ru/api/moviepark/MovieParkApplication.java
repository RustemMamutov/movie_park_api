package ru.api.moviepark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@Slf4j
public class MovieParkApplication {
    public static void main(String[] args) {
        initCustomApplicationProperties();

        SpringApplication application = new SpringApplication(MovieParkApplication.class);
        application.setAddCommandLineProperties(false);
        application.run(args);
    }

    private static void initCustomApplicationProperties() {
        try {
            Path yamlPath = Paths.get("application.yaml");
            log.info("Yaml file path: {}", yamlPath);
            if (Files.exists(yamlPath)) {
                log.info("Reading custom application file: {}", new String(Files.readAllBytes(yamlPath)));
                try (InputStream is = new FileInputStream(new File(yamlPath.toString()))){
                    System.getProperties().load(is);
                }
            } else {
                log.info("Custom application file doesn't exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
