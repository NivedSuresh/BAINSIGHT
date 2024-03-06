package com.exchange.matchingengine.MarketDataSimulation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
class MarketDataGenerator {

    private static final AtomicInteger integer = new AtomicInteger();
    private final MarketData marketData;

    public void sendUpdates(){
        if(!marketData.getSymbolQueue().isEmpty()) dequeAndCast();
        else pollAndCast();
    }

    private void pollAndCast() {
    }

    private void dequeAndCast() {
    }

    private String getExchange(){
        return integer.incrementAndGet() % 2 == 0 ? "NSE" : "BSE";
    }

}
