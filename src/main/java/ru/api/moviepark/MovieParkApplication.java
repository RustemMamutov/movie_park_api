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
            String dirtyPath = MovieParkApplication.class.getResource("").toString();
            String jarPath = dirtyPath
                    .replaceAll("^.*file:/", "")
                    .replaceAll("jar!.*", "jar")
                    .replaceAll("%20", " ");
            if (!jarPath.endsWith(".jar")) { // this is needed if you plan to run the app using Spring Tools Suit play button.
                jarPath = jarPath.replaceAll("/classes/.*", "/classes/");
            }

            Path yamlPath = Paths.get(jarPath).getParent().resolve("application.yaml");
            File yamlFile = new File(yamlPath.toString());
            if (yamlFile.exists()) {
                log.info("Прочитан пользовательный файл конфигурации: {}", new String(Files.readAllBytes(yamlPath)));
                try (InputStream is = new FileInputStream(yamlFile)){
                    System.getProperties().load(is);
                }
            } else {
                log.info("Отсутствует пользовательский файл конфигурации");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
