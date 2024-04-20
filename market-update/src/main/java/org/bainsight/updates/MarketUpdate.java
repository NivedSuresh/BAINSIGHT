package org.bainsight.updates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class MarketUpdate {

    public static void main(String[] args) {
        SpringApplication.run(MarketUpdate.class, args);
    }

}
