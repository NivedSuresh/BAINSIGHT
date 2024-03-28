package org.bainsight.market.Config.Redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

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