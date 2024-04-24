package org.bainsight.order.MatchingSim.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final ObjectMapper mapper;

//    @Value("${spring.data.redis.nodes}")
//    String[] redisNodes;

//    @Value("${spring.data.redis.cluster.max-redirects}")
//    private int maxRedirects;

//
//
//    @Bean
//    LettuceConnectionFactory redisConnectionFactory(RedisConfiguration redisConfiguration) {
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
////                .readFrom()  // Prefer replicas for reads (optional)
//                .commandTimeout(Duration.ofSeconds(120))  // Optional: Set command timeout
//                .build();
//
//        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfiguration, clientConfig);
//        connectionFactory.afterPropertiesSet();
//        return connectionFactory;
//    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisConfiguration){
        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisConfiguration, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    @Bean
    RedisStandaloneConfiguration redisConfiguration(){
        return new RedisStandaloneConfiguration("localhost");
    }
//
//    @Bean
//    RedisClusterConfiguration redisConfiguration() {
//        List<String> clusterNodes = Arrays.asList(redisNodes);
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
//        redisClusterConfiguration.setMaxRedirects(5);
//        return redisClusterConfiguration;
//    }
//
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