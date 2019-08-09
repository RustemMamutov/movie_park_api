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
        entityManagerFactoryRef = "remoteEntityManagerFactory",
        transactionManagerRef = "remoteTransactionManager",
        basePackages = {"ru.api.moviepark.data.remote"}
)
public class RemoteDatabaseConfig {

    private Environment env;

    public RemoteDatabaseConfig(Environment env) {
        this.env = env;
    }

    @Primary
    @Bean(name = "remoteDatasource")
    public DataSource customerDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("psql.datasource.driverClassName"));
        dataSource.setUrl(env.getProperty("psql.datasource.url"));
        dataSource.setUsername(env.getProperty("psql.datasource.username"));
        dataSource.setPassword(env.getProperty("psql.datasource.password"));

        return dataSource;
    }

    @Primary
    @Bean(name = "remoteEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("remoteDatasource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("ru.api.moviepark.data.remote")
                .build();
    }

    @Primary
    @Bean(name = "remoteTransactionManager")
    public PlatformTransactionManager customerTransactionManager(
            @Qualifier("remoteEntityManagerFactory") EntityManagerFactory customerEntityManagerFactory) {
        return new JpaTransactionManager(customerEntityManagerFactory);
    }
}
