package org.exchange.user.Config.Redis;


import org.exchange.user.Model.PrincipalRevoked;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String, PrincipalRevoked> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        StringRedisSerializer stringRedisSerializer = StringRedisSerializer.UTF_8;
        GenericToStringSerializer<PrincipalRevoked> longToStringSerializer = new GenericToStringSerializer<>(PrincipalRevoked.class);
        return new ReactiveRedisTemplate<>(factory,
                RedisSerializationContext.<String, PrincipalRevoked>newSerializationContext(jdkSerializationRedisSerializer)
                        .key(stringRedisSerializer).value(longToStringSerializer).build());
    }


}
