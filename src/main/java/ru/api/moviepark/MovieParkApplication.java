package ru.api.moviepark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@Slf4j
public class MovieParkApplication {
    public static void main(String[] args) {
        if(args.length > 0 && args[0] != null) {
            initCustomApplicationProperties(Paths.get(args[0]));
        }

        SpringApplication application = new SpringApplication(MovieParkApplication.class);
        application.setAddCommandLineProperties(false);
        application.run(args);
    }

    private static void initCustomApplicationProperties(Path filePath) {
        try {
            System.out.println("Custom application file path: {}" + filePath);
            System.out.println("Custom application file exists: {}" + filePath.toFile().exists());
            if (filePath.toFile().exists()) {
                System.out.println("Reading custom application file:\n{}" + new String(Files.readAllBytes(filePath)));
                try (InputStream is = Files.newInputStream(filePath)){
                    System.getProperties().load(is);
                }
            } else {
                System.out.println("Custom application file doesn't exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
