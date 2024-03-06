package com.exchange.riskmanagement.Config.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic addToBook() {
        return new NewTopic("add-order", 3, (short) 1);
    }

}
