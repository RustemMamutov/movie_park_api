package ru.api.moviepark.data.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "psqlEntityManagerFactory",
        transactionManagerRef = "psqlTransactionManager",
        basePackages = {"ru.api.moviepark.data"}
)
public class DatabaseConfig {

    private Environment env;

    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Primary
    @Bean(name = "psqlDatasource")
    public DataSource customerDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("psql.datasource.driverClassName"));
        dataSource.setUrl(env.getProperty("psql.datasource.url"));
        dataSource.setUsername(env.getProperty("psql.datasource.username"));
        dataSource.setPassword(env.getProperty("psql.datasource.password"));

        return dataSource;
    }

    @Primary
    @Bean(name = "psqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("psqlDatasource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("ru.api.moviepark.data")
                .build();
    }

    @Primary
    @Bean(name = "psqlTransactionManager")
    public PlatformTransactionManager customerTransactionManager(
            @Qualifier("psqlEntityManagerFactory") EntityManagerFactory customerEntityManagerFactory) {
        return new JpaTransactionManager(customerEntityManagerFactory);
    }
}
