package org.bainsight.liquidity;

import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
public class LiquidityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiquidityServiceApplication.class, args);
	}




}
