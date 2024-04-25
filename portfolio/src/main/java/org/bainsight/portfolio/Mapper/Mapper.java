package org.bainsight.portfolio.Mapper;


import org.bainsight.portfolio.Model.Dto.*;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.exchange.library.KafkaEvent.PortfolioUpdateEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mapper {


    public PortfolioDto portfolioEntityToDto(final Portfolio portfolio){

        List<PortfolioSymbolDto> portfolioSymbols = portfolio.getPortfolioSymbols().stream().map(portfolioSymbol ->
            PortfolioSymbolDto.builder()
                    .symbolQuantityId(portfolioSymbol.getSymbolQuantityId())
                    .soldAmount(portfolioSymbol.getSoldAmount())
                    .symbol(portfolioSymbol.getSymbol())
                    .investedAmount(portfolioSymbol.getInvestedAmount())
                    .quantity(portfolioSymbol.getQuantity())
                    .build()).toList();

        return PortfolioDto.builder()
                .ucc(portfolio.getUcc())
                .portfolioId(portfolio.getPortfolioId())
                .portfolioSymbols(portfolioSymbols)
                .build();
    }

    public WalletDto walletEntityToDto(final Wallet wallet, boolean fetchTransactions){
        List<TransactionDto> list = fetchTransactions ? wallet.getTransactions().stream().map(transaction -> TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .walletTransactionType(transaction.getWalletTransactionType())
                .timestamp(transaction.getTimestamp())
                .build()).toList() : List.of();
        return WalletDto.builder()
                .walletId(wallet.getWalletId())
                .currentBalance(wallet.getCurrentBalance())
                .transactions(list)
                .withdrawableBalance(wallet.getAvailableBalance())
                .build();
    }


    public PortfolioUpdateRequest getPortfolioUpdateRequest(PortfolioUpdateEvent updateEvent){
        double total = updateEvent.getPricePerShare() * updateEvent.getQuantity();
        return PortfolioUpdateRequest.builder()
                .ucc(updateEvent.getUcc().toString())
                .price(total)
                .quantity(updateEvent.getQuantity())
                .symbol(updateEvent.getSymbol())
                .build();
    }


    public PortfolioSymbolDto toPortfolioSymbolDto(PortfolioSymbol portfolioSymbol) {
        return PortfolioSymbolDto.builder()
                .symbolQuantityId(portfolioSymbol.getSymbolQuantityId())
                .soldAmount(portfolioSymbol.getSoldAmount())
                .symbol(portfolioSymbol.getSymbol())
                .investedAmount(portfolioSymbol.getInvestedAmount())
                .quantity(portfolioSymbol.getQuantity())
                .build();
    }
}
