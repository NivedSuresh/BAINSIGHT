package org.bainsight.liquidity;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
public class MarketData {

	public static void main(String[] args) {
		SpringApplication.run(MarketData.class, args);
	}




}
