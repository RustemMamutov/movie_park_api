package ru.api.moviepark.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"ru.api.moviepark.data"})
public class DatabaseConfig {

    private Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    @Profile("prod")
    public DataSource customerProdDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.prod.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.prod.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.prod.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.prod.password"));

        return dataSource;
    }

    @Bean
    @Profile({"dev", "default"})
    public DataSource customerDevDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.dev.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.dev.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.dev.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.dev.password"));

        return dataSource;
    }
}
