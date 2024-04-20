package org.bainsight.portfolio.Model.Dto;


import lombok.Builder;
import org.exchange.library.Enums.TransactionType;

@Builder
public record PortfolioUpdateRequest(
        String ucc,
        String symbol,
        Long quantity,
        Double price
) {}
