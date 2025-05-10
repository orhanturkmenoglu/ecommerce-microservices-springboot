package com.example.auth_service.config;

import com.example.auth_service.service.SecretManagerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final SecretManagerService secretManagerService;

    @Value("${aws.db-config-secret-name}")
    private String dbConfigSecretName;

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() throws JsonProcessingException {
        String secret = secretManagerService.getSecret(dbConfigSecretName);


        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> dbConfig = objectMapper.readValue(secret, Map.class);

        String host = dbConfig.get("host");
        String port = dbConfig.get("port");
        String dbname = dbConfig.get("dbname");
        String username = dbConfig.get("username");
        String password = dbConfig.get("password");

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password not found in AWS Secret");
        }

        String url = String.format("jdbc:mysql://%s:%s/%s", host, port, dbname);

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
