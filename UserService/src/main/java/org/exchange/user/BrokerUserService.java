package org.exchange.user;

import org.exchange.user.Security.JWT.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class BrokerUserService {

    public static void main(String[] args) {
        SpringApplication.run(BrokerUserService.class, args);
    }

}
