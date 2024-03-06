package com.exchange.matchingengine.MarketDataSimulation;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

@Getter
@Component
class MarketData {

    private final Queue<String> symbolQueue;
    private final Stack<String> symbolStack;


    MarketData() {
        this.symbolQueue = new ArrayDeque<>();
        this.symbolStack = new Stack<>();
        insertSymbolsIntoQueue();
    }

    private void insertSymbolsIntoQueue() {
        List.of(
                "AAPL", "MSFT", "GOOGL", "AMZN", "FB",   // Tech companies
                "JPM", "BAC", "WFC", "C", "GS",          // Banks
                "TSLA", "NVDA", "NFLX", "INTC", "AMD",   // Tech & entertainment
                "KO", "PEP", "MCD", "SBUX", "CMG",       // Food & beverages
                "DIS", "TWTR", "SNAP", "UBER", "LYFT",   // Entertainment & transportation
                "WMT", "TGT", "AMZN", "COST", "HD",      // Retail
                "BA", "LMT", "RTX", "GD", "NOC",         // Aerospace & defense
                "CVS", "WBA", "CI", "UNH", "ANTM",       // Healthcare
                "MMM", "CAT", "JNJ", "PFE", "MRK",       // Industrial & healthcare
                "GS", "MS", "C", "JPM", "BAC",           // Financial services
                "AMGN", "GILD", "BIIB", "REGN", "VRTX",  // Biotechnology & pharmaceuticals
                "V", "MA", "AXP", "PYPL", "SQ",          // Payment processing & fintech
                "SPY", "QQQ", "DIA", "IWM", "GLD",       // ETFs
                "XOM", "CVX", "BP", "TOT", "RDS.A",      // Oil & gas
                "IBM", "ORCL", "CRM", "SAP", "ADBE",     // Software & cloud computing
                "ATVI", "EA", "TTWO", "NTDOY", "UBSFF",  // Gaming
                "T", "VZ", "TMUS", "CHTR", "CMCSA",     // Telecommunications & media
                "INTU", "PAYX", "ADP", "FIS", "FISV",   // Financial technology
                "NOW", "CRM", "ZM", "SNOW", "TEAM"      // Cloud & software
        ).forEach(symbolQueue::offer);
    }


}
