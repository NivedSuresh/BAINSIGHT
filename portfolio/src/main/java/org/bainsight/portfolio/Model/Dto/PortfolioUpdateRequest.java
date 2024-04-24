package org.bainsight.portfolio.Model.Dto;


import lombok.Builder;

@Builder
public record PortfolioUpdateRequest(
        String ucc,
        String symbol,
        Long quantity,
        Double price
) {}
