package org.bainsight.portfolio.Jobs;

import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetJob {

    private final PortfolioService portfolioService;
    private final WalletService walletService;


    @Job(name = "reset-wallet", retries = 5)
    public void resetWallet(){
        this.walletService.resetAvailableBalance();
    }

    @Job(name = "reset-portfolio", retries = 5)
    public void resetPortfolio(){
        this.portfolioService.resetOpenOrders();
    }

}
