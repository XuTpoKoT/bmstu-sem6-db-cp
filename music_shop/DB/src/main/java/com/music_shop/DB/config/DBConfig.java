package com.music_shop.DB.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import org.postgresql.Driver;

@Configuration
public class DBConfig {
    //@Value("#{systemProperties['db.url']}")
    @Value("${db.url}")
    private String PG_DB_URL;
    //@Value("#{systemProperties['db.username']}")
    @Value("${db.username}")
    private String PG_DB_USERNAME;
    //@Value("#{systemProperties['db.password']}")
    @Value("${db.password}")
    private String PG_DB_PASSWORD;


    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(Driver.class);
        dataSource.setUrl(PG_DB_URL);
        dataSource.setUsername(PG_DB_USERNAME);
        dataSource.setPassword(PG_DB_PASSWORD);

        return dataSource;
    }

    @Bean
    NamedParameterJdbcTemplate myJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }
}
