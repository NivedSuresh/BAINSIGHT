package org.bainsight.history.Config.HazelCast;

import com.hazelcast.config.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HazelCastConfig {


    @Bean
    public Config config(){
        Config config = this.getConfig();

        /*  EVICTION CONFIG */
        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        evictionConfig.setSize(1000000);
        evictionConfig.setEvictionPolicy(EvictionPolicy.LFU);

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("candle_sticks")
                .setEvictionConfig(evictionConfig);

        config.addMapConfig(mapConfig);

        return config;
    }

    private Config getConfig() {
        Config config = new Config();

        config.setClusterName("history_service_hazelcast");

        /*  NETWORK CONFIG */
        MulticastConfig multicastConfig = new MulticastConfig();
        multicastConfig.setEnabled(false);

        AutoDetectionConfig autoDetectionConfig = new AutoDetectionConfig();
        autoDetectionConfig.setEnabled(false);

        JoinConfig joinConfig = new JoinConfig();
        joinConfig.setAutoDetectionConfig(autoDetectionConfig);
        joinConfig.setMulticastConfig(multicastConfig);


        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setJoin(joinConfig);

        config.setNetworkConfig(networkConfig);
        return config;
    }


}
