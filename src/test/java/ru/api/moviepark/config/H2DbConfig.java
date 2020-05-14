package ru.api.moviepark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@Profile("test")
public class H2DbConfig {

    @Bean
    public DataSource customerProdDataSource() throws IOException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        dataSource.setUsername("username");
        dataSource.setPassword("password");

        JdbcTemplate template = new JdbcTemplate(dataSource);

        Path configDirPath = ResourceUtils.getFile(getClass().getResource("").getPath()).toPath();
        String initQuery = new String(Files.readAllBytes(configDirPath.resolve("create_tables_H2.sql")));
        String fillDataQuery = new String(Files.readAllBytes(configDirPath.resolve("fill_data.sql")));

        template.execute(initQuery);
        template.execute(fillDataQuery);

        return dataSource;
    }
}
