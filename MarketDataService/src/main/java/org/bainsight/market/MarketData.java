package org.bainsight.market;

import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import lombok.RequiredArgsConstructor;
import org.bainsight.market.Config.Redis.CandleStickKeySpaceConfig;
import org.bainsight.market.Model.Entity.CandleStick;
import org.bainsight.market.Repository.CandleStickRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableRedisEnhancedRepositories(basePackages = "org.bainsight.market", keyspaceConfiguration = CandleStickKeySpaceConfig.class)
public class MarketData {
    public MarketData(CandleStickRepo candleStickRepo, RedisTemplate<String, Object> redisTemplate) {
        this.candleStickRepo = candleStickRepo;
        this.redisTemplate = redisTemplate;
    }

    public static void main(String[] args) {

		/* TODO : REMOVE BEFORE DEPLOYING */
		System.setProperty("aeron.sample.stream.id", "1001");
		System.setProperty("aeron.stick.multicast.channel", "aeron:udp?endpoint=224.0.1.1:40456|interface=localhost|reliable=true");
		System.setProperty("aeron.sample.embeddedMediaDriver", "true");

		SpringApplication.run(MarketData.class, args);
	}

	private final CandleStickRepo candleStickRepo;
	private final RedisTemplate<String, Object> redisTemplate;

//	@Bean
//	public CommandLineRunner commandLineRunner(){
//		return args -> {
//			List<String> symbols = List.of(
//					"AAPL", "MSFT", "GOOGL", "AMZN", "FB", // Tech companies
//					"JPM", "BAC", "WFC", "C", "GS",          // Banks
////					"TSLA", "NVDA", "NFLX", "INTC", "AMD",   // Tech & entertainment
//					"KO", "PEP", "MCD", "SBUX", "CMG",       // Food & beverages
//					"DIS", "TWTR", "SNAP", "UBER", "LYFT",   // Entertainment & transportation
//					"WMT", "TGT", "AMZN", "COST", "HD"     // Retail
//			);
//
//			Set<String> jvmKeys = new HashSet<>(symbols);
//
//
//		};
//	}

}
