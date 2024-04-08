package org.bainsight.history.Simulation;


public record SymbolMeta(
        String symbol,
        double changePer5Minute,
        double startPrice
) {}
