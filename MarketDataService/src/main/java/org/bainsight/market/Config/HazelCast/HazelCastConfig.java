package org.bainsight.market.Config.HazelCast;

import com.hazelcast.config.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HazelCastConfig {


    @Bean
    public Config config(){
        Config config = new Config();
        config.setClusterName("symbol_status");

        /*  NETWORK CONFIG */
        MulticastConfig multicastConfig = new MulticastConfig();
        multicastConfig.setEnabled(true);

        JoinConfig joinConfig = new JoinConfig();
        joinConfig.setMulticastConfig(multicastConfig);

        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setJoin(joinConfig);

        config.setNetworkConfig(networkConfig);


        /*  EVICTION CONFIG */
        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setSize(5000);
        evictionConfig.setEvictionPolicy(EvictionPolicy.LFU);

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("symbol_status")
                .setEvictionConfig(evictionConfig);

        config.addMapConfig(mapConfig);

        return config;
    }


}
