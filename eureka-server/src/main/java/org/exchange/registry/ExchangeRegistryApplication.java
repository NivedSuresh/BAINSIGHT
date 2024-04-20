package org.exchange.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ExchangeRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRegistryApplication.class, args);
    }

}
