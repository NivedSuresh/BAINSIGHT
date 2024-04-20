package org.bainsight.portfolio.Mapper;


import org.bainsight.portfolio.Model.Dto.PortfolioDto;
import org.bainsight.portfolio.Model.Dto.PortfolioSymbolDto;
import org.bainsight.portfolio.Model.Dto.TransactionDto;
import org.bainsight.portfolio.Model.Dto.WalletDto;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Mapper {


    public PortfolioDto portfolioEntityToDto(final Portfolio portfolio){

        List<PortfolioSymbolDto> portfolioSymbols = portfolio.getPortfolioSymbols().stream().map(portfolioSymbol ->
            PortfolioSymbolDto.builder()
                    .symbolQuantityId(portfolioSymbol.getSymbolQuantityId())
                    .portfolioId(portfolio.getPortfolioId())
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




}
