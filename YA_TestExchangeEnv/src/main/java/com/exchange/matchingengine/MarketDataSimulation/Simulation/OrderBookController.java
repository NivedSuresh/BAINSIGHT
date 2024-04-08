package com.exchange.matchingengine.MarketDataSimulation.Simulation;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequestMapping("/stock/orderbook")
@RestController
public class OrderBookController {


    private final MarketData marketData;
    private final Gson gson;

    OrderBookController(MarketData marketData, Gson gson) {
        this.marketData = marketData;
        this.gson = gson;

    }


}
