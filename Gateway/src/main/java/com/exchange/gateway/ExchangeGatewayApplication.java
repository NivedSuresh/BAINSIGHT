package com.exchange.gateway;

import com.exchange.gateway.Security.Jwt.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication()
@EnableConfigurationProperties(RsaKeyProperties.class)
@CrossOrigin(origins = "*", allowCredentials = "true")
public class ExchangeGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeGatewayApplication.class, args);
    }

}
