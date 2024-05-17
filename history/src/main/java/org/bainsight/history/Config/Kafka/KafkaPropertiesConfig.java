package org.bainsight.history.Config.Kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.exchange.library.KafkaEvent.DailyOrderMetaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaPropertiesConfig {


    private final String kafkaServer;

    public KafkaPropertiesConfig(@Value("${kafka.bootstrap-servers}") final String kafkaServer) {
        this.kafkaServer = kafkaServer;
    }

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> kafkaProps = new HashMap<>();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, "portfolio-validation-rollback");
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        kafkaProps.put("spring.json.type.mapping", DailyOrderMetaEvent.class);
        return kafkaProps;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(final Map<String, Object> consumerConfig) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig);
    }

}
