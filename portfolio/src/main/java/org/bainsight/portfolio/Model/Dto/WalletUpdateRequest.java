package org.bainsight.portfolio.Model.Dto;


import lombok.Builder;


@Builder
public record WalletUpdateRequest(
        Double changeInBalance,
        Double changeInAvailableBalance
) {}
