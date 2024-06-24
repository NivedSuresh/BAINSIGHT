package org.bainsight.updates;

import org.bainsight.updates.Config.Security.JWT.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
@EnableConfigurationProperties(value = RsaKeyProperties.class)
public class MarketUpdate {

    public static void main(String[] args) {
        SpringApplication.run(MarketUpdate.class, args);
    }

}
