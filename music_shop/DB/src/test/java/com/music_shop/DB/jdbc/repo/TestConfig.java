package com.music_shop.DB.jdbc.repo;

import com.music_shop.DB.API.CardRepo;
import com.music_shop.DB.API.OrderRepo;
import com.music_shop.DB.API.ProductRepo;
import com.music_shop.DB.API.UserRepo;
import com.music_shop.DB.jdbc.mapper.*;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import javax.sql.DataSource;

@Configuration
public class TestConfig {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer;
    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
                .withDatabaseName("integration-tests-db")
                .withUsername("postgres")
                .withPassword("sa")
                .withExposedPorts(5432);
        postgreSQLContainer.start();
        System.out.println(postgreSQLContainer.getJdbcUrl());
    }
    
    @Bean
    public DataSource dataSource() {
        System.out.println("data source");
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(Driver.class);
        dataSource.setUrl(postgreSQLContainer.getJdbcUrl());
        dataSource.setUsername(postgreSQLContainer.getUsername());
        dataSource.setPassword(postgreSQLContainer.getPassword());

        return dataSource;
    }

    @Bean
    NamedParameterJdbcTemplate jdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    @Bean
    UserRepo userRepoImpl() {
        return new UserRepoImpl(jdbcTemplate(), userMapper());
    }

    @Bean
    ProductRepo productRepoImpl() {
        return new ProductRepoImpl(jdbcTemplate(), productMapper());
    }

    @Bean
    CardRepo cardRepoImpl() {
        return new CardRepoImpl(jdbcTemplate(), cardMapper());
    }

    @Bean
    OrderRepo orderRepoImpl() {
        return new OrderRepoImpl(jdbcTemplate(), orderMapper(), orderDetailsMapper());
    }

    @Bean
    UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    ProductMapper productMapper() {
        return new ProductMapper();
    }

    @Bean
    IntMapper intMapper() {
        return new IntMapper();
    }

    @Bean
    CardMapper cardMapper() {
        return new CardMapper();
    }

    @Bean
    OrderMapper orderMapper() {
        return new OrderMapper();
    }

    @Bean
    OrderDetailsMapper orderDetailsMapper() {
        return new OrderDetailsMapper();
    }
}
