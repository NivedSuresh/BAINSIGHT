package com.exchange.riskmanagement.Model.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchange-order-uri")
public record ExchangeOrderURI(String NSE, String BSE) {}
