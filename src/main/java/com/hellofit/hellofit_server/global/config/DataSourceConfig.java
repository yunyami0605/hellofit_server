package com.hellofit.hellofit_server.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() throws Exception {
        SecretsManagerClient client = SecretsManagerClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .build();

        GetSecretValueResponse response = client.getSecretValue(
            GetSecretValueRequest.builder()
                .secretId("hellofit/prod/db")
                .build()
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode secretJson = mapper.readTree(response.secretString());

        // DB 설정
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(secretJson.get("url")
            .asText());
        config.setUsername(secretJson.get("username")
            .asText());
        config.setPassword(secretJson.get("password")
            .asText());
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        // pepper → Spring 환경 변수처럼 등록
        String pepper = secretJson.path("password_pepper")
            .asText(null);
        if (pepper != null) {
            System.setProperty("hellofit.security.pepper", pepper);
        }

        return new HikariDataSource(config);
    }
}
