package org.bainsight.order;

import org.bainsight.order.MatchingSim.Redis.CandleStickKeySpaceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories(basePackages = {"org.bainsight.order.MatchingSim.Repo"}, keyspaceConfiguration = CandleStickKeySpaceConfig.class)
public class OrderPersistence {

    public static void main(String[] args) {
        SpringApplication.run(OrderPersistence.class, args);
    }

}
