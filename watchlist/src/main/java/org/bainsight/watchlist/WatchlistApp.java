package org.bainsight.watchlist;

import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import org.bainsight.watchlist.Config.Redis.CandleStickKeySpaceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * Depends PostgreSQL and Redis
 * */
@SpringBootApplication
@EnableRedisEnhancedRepositories(basePackages = "org.bainsight.watchlist.CandleStick.Data", keyspaceConfiguration = CandleStickKeySpaceConfig.class)
public class WatchlistApp {

	public static void main(String[] args) {
		SpringApplication.run(WatchlistApp.class, args);
	}

}
