package com.exchange.matchingengine.MarketDataSimulation.Simulation;

import com.google.gson.Gson;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

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
        List<Tick> allTicks = new ArrayList<>(marketData.getExchangeSpecificOrderBook().get("NSE"));
        allTicks.addAll(marketData.getExchangeSpecificOrderBook().get("BSE"));
        return Flux.create(fluxSink -> {
            for(Tick tick : allTicks){
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
