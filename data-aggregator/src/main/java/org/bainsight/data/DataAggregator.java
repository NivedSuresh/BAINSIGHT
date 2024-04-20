package org.bainsight.data;


import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import org.bainsight.data.Config.Redis.CandleStickKeySpaceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableRedisEnhancedRepositories(basePackages = "org.bainsight.data.Persistence.*", keyspaceConfiguration = CandleStickKeySpaceConfig.class)
public class DataAggregator {

    public static void main(String[] args) {

        /* TODO : REMOVE BEFORE DEPLOYING */
        System.setProperty("aeron.sample.stream.id", "1001");
        System.setProperty("aeron.stick.multicast.channel", "aeron:udp?endpoint=224.0.1.1:40456|interface=localhost|reliable=true");
        System.setProperty("aeron.sample.embeddedMediaDriver", "true");

        SpringApplication.run(DataAggregator.class, args);

    }

}
