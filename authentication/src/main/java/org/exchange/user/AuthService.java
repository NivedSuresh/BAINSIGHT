package org.exchange.user;

import org.exchange.user.Security.JWT.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;


/**
 * Depends on Postgres
 * */
@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@CrossOrigin(origins = "*", allowCredentials = "true")
@EnableR2dbcRepositories
public class AuthService {
    public static void main(String[] args) {
        SpringApplication.run(AuthService.class, args);
    }

}
