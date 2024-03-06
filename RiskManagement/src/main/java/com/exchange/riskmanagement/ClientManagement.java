package com.exchange.riskmanagement;

import com.exchange.riskmanagement.Model.Properties.ExchangeOrderURI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ExchangeOrderURI.class)
@SpringBootApplication(scanBasePackages = {"com.exchange.riskmanagement.*", "org.exchange.*"})
public class ClientManagement {
    public static void main(String[] args) {
        SpringApplication.run(ClientManagement.class, args);
    }

}

