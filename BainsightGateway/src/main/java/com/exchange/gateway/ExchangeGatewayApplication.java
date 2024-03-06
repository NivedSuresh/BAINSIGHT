package com.exchange.gateway;

import com.exchange.gateway.Security.Jwt.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication()
@EnableConfigurationProperties(RsaKeyProperties.class)
public class ExchangeGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeGatewayApplication.class, args);
    }

}
