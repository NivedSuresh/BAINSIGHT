package com.bainsight.risk.Config.Redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisClusterConfig {


    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisConfiguration){
        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofSeconds(60))
                .useSsl()
                .build();
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisConfiguration, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        connectionFactory.start();
        return connectionFactory;
    }

    @Bean
    RedisStandaloneConfiguration redisConfiguration(@Value("${spring.data.redis.host:localhost}") final String hostname,
                                                    @Value("${spring.data.redis.password}") final String password){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(hostname);
        configuration.setPassword(password);
        configuration.setPort(6380);
        return configuration;
    }


    @Bean
    public RedisTemplate<String, Object> template(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

}