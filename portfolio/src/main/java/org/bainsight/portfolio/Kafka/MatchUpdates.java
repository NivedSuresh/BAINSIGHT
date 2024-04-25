package org.bainsight.portfolio.Kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.exchange.library.KafkaEvent.PortfolioUpdateEvent;
import org.exchange.library.Utils.BainsightUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchUpdates {

    private final ObjectMapper objectMapper;
    private final Mapper modelMapper;
    private final PortfolioService portfolioService;
    private final WalletService walletService;


    @KafkaListener(topics = "update-portfolio", groupId = "portfolio")
    public void listenToMatches(String match){
        try
        {
            PortfolioUpdateEvent updateEvent = this.objectMapper.readValue(match, PortfolioUpdateEvent.class);
            log.info("Update Event: {}", updateEvent);
            if(updateEvent.isBid()) { this.updateAfterBid(updateEvent); }
            else { this.updateAfterAsk(updateEvent); }
        }
        catch (Exception e)
        {
            /* TODO: IMPLEMENT JOURNALING */
            e.printStackTrace();
        }
    }

    private void updateAfterAsk(PortfolioUpdateEvent updateEvent) {
        PortfolioUpdateRequest portfolioUpdateRequest = this.modelMapper.getPortfolioUpdateRequest(updateEvent);
        this.updatePortfolioAfterAskMatchWithRetry(portfolioUpdateRequest, 1);

        WalletUpdateRequest updateRequest = new WalletUpdateRequest(portfolioUpdateRequest.price(), portfolioUpdateRequest.price());
        this.updateWalletBalanceWithRetry(updateEvent.getUcc(), updateRequest, 1, 1000);
    }

    private void updateAfterBid(PortfolioUpdateEvent updateEvent) {

        PortfolioUpdateRequest portfolioUpdateRequest = this.modelMapper.getPortfolioUpdateRequest(updateEvent);

        WalletUpdateRequest updateRequest = new WalletUpdateRequest(-portfolioUpdateRequest.price(),0.0);
        this.updateWalletBalanceWithRetry(updateEvent.getUcc(), updateRequest, 1, 1000);

        this.updatePortfolioAfterBidMatchWithRetry(portfolioUpdateRequest, 1);
    }


    public void updateWalletBalanceWithRetry(UUID ucc, WalletUpdateRequest updateRequest, int tryCount, int sleepInMillis){
        try{ this.walletService.updateWalletBalance(ucc, updateRequest); }
        catch (ObjectOptimisticLockingFailureException ex){
            if(tryCount >= 3) throw  ex;
            BainsightUtils.sleep(sleepInMillis);
            this.updateWalletBalanceWithRetry(ucc, updateRequest, tryCount + 1, sleepInMillis);
        }
    }


    public void updatePortfolioAfterAskMatchWithRetry(PortfolioUpdateRequest portfolioUpdateRequest, int tryCount){
        try{
            this.portfolioService.updatePortfolioAfterAskMatch(portfolioUpdateRequest);
        }
        catch (ObjectOptimisticLockingFailureException ex){
            if(tryCount >= 3) throw ex;
            this.updatePortfolioAfterAskMatchWithRetry(portfolioUpdateRequest, tryCount + 1);
        }
    }


    public void updatePortfolioAfterBidMatchWithRetry(PortfolioUpdateRequest portfolioUpdateRequest, int tryCount){
        try{
            this.portfolioService.updatePortfolioAfterBidMatch(portfolioUpdateRequest);
        }
        catch (ObjectOptimisticLockingFailureException ex){
            if(tryCount >= 3) throw ex;
            this.updatePortfolioAfterBidMatchWithRetry(portfolioUpdateRequest, tryCount + 1);
        }
    }


}
