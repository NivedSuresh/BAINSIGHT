package org.bainsight.liquidity.Config.Listener;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bainsight.liquidity.Adapters.InstantAdapter;
import org.exchange.library.Dto.MarketRelated.Depth;
import org.exchange.library.Dto.MarketRelated.MarketDepth;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class GsonConfig {
    @Bean
    public Gson gson(){
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    @Bean
    public Kryo kryo(){
        Kryo kryo = new Kryo();
        kryo.register(Depth.class);
        kryo.register(MarketDepth.class);
        kryo.register(Tick.class);
        return kryo;
    }

}
