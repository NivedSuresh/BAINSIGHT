package org.bainsight.portfolio;

import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.PortfolioService;
import org.bainsight.portfolio.Data.WalletService;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class PortfolioApplication {


    private final PortfolioService portfolioService;
    private final WalletService walletService;


    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            Portfolio portfolio = this.portfolioService.fetchUserPortfolio("UCC");
            Wallet wallet = this.walletService.fetchWallet("UCC");

        };
    }

}
