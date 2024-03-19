package com.exchange.matchingengine.MarketDataSimulation.Simulation;

import com.exchange.matchingengine.MarketDataSimulation.Enums.PushTo;
import com.exchange.matchingengine.MarketDataSimulation.Models.TickerEx;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("/stock/orderbook")
@RestController
public class OrderBookController {


    private final MarketData marketData;
    private final Gson gson;

    OrderBookController(MarketData marketData, Gson gson) {
        this.marketData = marketData;
        this.gson = gson;

    }


    /**
     * Endpoint to request for the OrderBook
     * */
    @GetMapping
    public Flux<byte[]> getOrderBook(){
        return Flux.create(fluxSink -> {
            for(Tick tick : marketData.getOrderBookSim()){
                if(tick == null) {
                    fluxSink.complete();
                    return;
                }
                byte[] buffer = gson.toJson(tick).getBytes();
                fluxSink.next(buffer);
            }
        });
    }


}
