package ru.api.moviepark.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "EntityManagerFactory",
        transactionManagerRef = "TransactionManager",
        basePackages = {"ru.api.moviepark.data"}
)
public class DatabaseConfig {

    private final Environment env;

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
    @Profile("local")
    public DataSource customerDevDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.local.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.local.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.local.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.local.password"));

        return dataSource;
    }

    @Bean(name = "EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("ru.api.moviepark.data")
                .build();
    }

    @Bean(name = "TransactionManager")
    public PlatformTransactionManager customerTransactionManager(
            @Qualifier("EntityManagerFactory") EntityManagerFactory customerEntityManagerFactory) {
        return new JpaTransactionManager(customerEntityManagerFactory);
    }
}
