package org.exchange.user;

import org.exchange.user.Security.JWT.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@CrossOrigin(origins = "*", allowCredentials = "true")
public class AuthService {

    public static void main(String[] args) {
        SpringApplication.run(AuthService.class, args);
    }

}
