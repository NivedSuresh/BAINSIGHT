package com.bainsight.risk;

import com.bainsight.risk.Config.Redis.CandleStickKeySpaceConfig;
import com.bainsight.risk.Data.CandleStickRepo;
import com.bainsight.risk.Data.DailyOrderMetaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;




/**
* DEPENDS ON REDIS.
* */
@SpringBootApplication
@EnableRedisRepositories(basePackages = {"com.bainsight.risk.Data"}, keyspaceConfiguration = CandleStickKeySpaceConfig.class)
@RequiredArgsConstructor
public class RiskManagement {


    private final DailyOrderMetaRepo dailyOrderMetaRepo;
    private final CandleStickRepo candleStickRepo;

    public static void main(String[] args) {
        SpringApplication.run(RiskManagement.class, args);
    }


    /*TODO: REMOVE*/

    @Bean
    @Profile("reset_cache")
    public CommandLineRunner commandLineRunner(){
        return args -> {
            this.candleStickRepo.deleteAll();
            this.dailyOrderMetaRepo.deleteAll();
        };
    }

}

