package org.bainsight.market.Config.Redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class RedisClusterConfig {

//    @Value("${spring.data.redis.nodes}")
//    String[] redisNodes;

//    @Value("${spring.data.redis.cluster.max-redirects}")
//    private int maxRedirects;

//
//
//    @Bean
//    LettuceConnectionFactory redisConnectionFactory(RedisClusterConfiguration redisConfiguration) {
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .readFrom(ReadFrom.REPLICA_PREFERRED)  // Prefer replicas for reads (optional)
//                .commandTimeout(Duration.ofSeconds(120))  // Optional: Set command timeout
//                .build();
//
//        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfiguration, clientConfig);
//        connectionFactory.afterPropertiesSet();
//        return connectionFactory;
//    }
//
//    @Bean
//    RedisClusterConfiguration redisConfiguration() {
//        List<String> clusterNodes = Arrays.asList(redisNodes);
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
//        redisClusterConfiguration.setMaxRedirects(5);
//        return redisClusterConfiguration;
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> template(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new JdkSerializationRedisSerializer());
//        template.setValueSerializer(new JdkSerializationRedisSerializer());
//        template.setEnableTransactionSupport(true);
//        template.afterPropertiesSet();
//        return template;
//    }

}