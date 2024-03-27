package org.bainsight.market;

import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import lombok.RequiredArgsConstructor;
import org.bainsight.market.Model.Dto.CandleStick;
import org.bainsight.market.Repository.CandleStickRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
@EnableScheduling
@EnableRedisEnhancedRepositories(basePackages = "org.bainsight.market")
public class MarketData {

	private final CandleStickRepo candleStickRepo;

	public static void main(String[] args) {

		/* TODO : REMOVE BEFORE DEPLOYING */
		System.setProperty("aeron.sample.stream.id", "1001");
		System.setProperty("aeron.stick.multicast.channel", "aeron:udp?endpoint=224.0.1.1:40456|interface=localhost|reliable=true");
		System.setProperty("aeron.sample.embeddedMediaDriver", "true");

		SpringApplication.run(MarketData.class, args);
	}


}
