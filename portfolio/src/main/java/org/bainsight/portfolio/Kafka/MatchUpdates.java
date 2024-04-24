package org.bainsight.portfolio.Kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.portfolio.Data.PortfolioService;
import org.bainsight.portfolio.Data.WalletService;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.exchange.library.KafkaEvent.PortfolioUpdateEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
        this.portfolioService.updatePortfolioAfterAskMatch(portfolioUpdateRequest);

        WalletUpdateRequest updateRequest = new WalletUpdateRequest(portfolioUpdateRequest.price(), portfolioUpdateRequest.price());
        this.walletService.updateWalletBalance(updateEvent.getUcc(), updateRequest, 1);
    }

    private void updateAfterBid(PortfolioUpdateEvent updateEvent) {

        PortfolioUpdateRequest portfolioUpdateRequest = this.modelMapper.getPortfolioUpdateRequest(updateEvent);

        WalletUpdateRequest updateRequest = new WalletUpdateRequest(-portfolioUpdateRequest.price(),0.0);
        this.walletService.updateWalletBalance(updateEvent.getUcc(), updateRequest, 1);

        this.portfolioService.updatePortfolioAfterBidMatch(portfolioUpdateRequest);
    }


}
