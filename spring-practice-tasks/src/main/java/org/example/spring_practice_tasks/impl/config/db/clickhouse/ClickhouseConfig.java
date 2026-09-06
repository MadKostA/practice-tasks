package org.example.spring_practice_tasks.impl.config.db.clickhouse;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClickhouseConfig {

    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties(prefix = "clickhouse")
    public DataSource clickhouseDataSource(ClickhouseProperties clickhouseProperties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(clickhouseProperties.getUrl());
        dataSource.setUsername(clickhouseProperties.getUser());
        dataSource.setPassword(clickhouseProperties.getPassword());
        dataSource.setDriverClassName(clickhouseProperties.getDriverClassName());
        return dataSource;
    }

    @Bean(name = "clickhouseJdbcTemplate")
    public JdbcTemplate clickhouseJdbcTemplate(@Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public Flyway clickhouseFlyway(@Qualifier("clickhouseDataSource") DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:migrations/clickhouse")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load();
        flyway.migrate();
        return flyway;
    }
}