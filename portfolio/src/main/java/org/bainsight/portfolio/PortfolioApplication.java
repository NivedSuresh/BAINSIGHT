package org.bainsight.portfolio;

import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.exchange.library.Exception.BadRequest.InvalidStateException;
import org.exchange.library.Utils.STRINGS;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
@RequiredArgsConstructor
public class PortfolioApplication {


    private final PortfolioService portfolioService;
    private final WalletService walletService;


    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }



    /* TODO: REMOVE BEFORE DEPLOYING */
    @Bean
    public CommandLineRunner commandLineRunner(){
        try{
            return args -> {

                final String ucc = STRINGS.UCC;
                Portfolio portfolio = this.portfolioService.fetchUserPortfolio(ucc);
                Wallet wallet = this.walletService.fetchWallet(ucc);


                if(wallet.getCurrentBalance() < 10000) {
                    WalletUpdateRequest walletUpdateRequest = WalletUpdateRequest
                            .builder()
                            .changeInAvailableBalance(1000000.0)
                            .changeInBalance(1000000.0)
                            .build();

                    this.walletService.updateWalletBalance(UUID.fromString(ucc), walletUpdateRequest);
                }

                List<PortfolioSymbol> portfolioSymbols = portfolio.getPortfolioSymbols();

                if(portfolioSymbols.size() < 2){
                    PortfolioUpdateRequest updateRequest1 = PortfolioUpdateRequest.builder()
                            .ucc(ucc)
                            .price(10000.0)
                            .quantity(1000L)
                            .symbol("AAPL"
                            )
                            .build();

                    PortfolioUpdateRequest updateRequest2 = PortfolioUpdateRequest.builder()
                            .ucc(ucc)
                            .price(12000.0)
                            .quantity(1090L)
                            .symbol("GOOGL")
                            .build();

                    this.portfolioService.updatePortfolioAfterBidMatch(updateRequest1);
                    this.portfolioService.updatePortfolioAfterBidMatch(updateRequest2);
                }
                else {
                    for(PortfolioSymbol portfolioSymbol : portfolioSymbols){
                        portfolioSymbol.setQuantity(1000L);
                        portfolioSymbol.setInvestedAmount(10000.0);
                        portfolioSymbol.setSoldAmount(0.0);
                        portfolioSymbol.setOpenQuantity(0L);
                    }
                    portfolioService.save(portfolio);
                }

            };
        }
        catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException();
        }
    }

}
