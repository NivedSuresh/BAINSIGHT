package org.bainsight.portfolio.Kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.exchange.library.KafkaEvent.WalletUpdateEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletValidationMarketBid {


    private final ObjectMapper objectMapper;
    private final WalletService walletService;
    private final PortfolioService portfolioService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "wallet-updation-market-bid", groupId = "portfolio")
    public void deductBalance(String update){
        try
        {
            log.info("New match for market bid. {}", update);
            WalletUpdateEvent walletUpdateEvent = this.objectMapper.readValue(update, WalletUpdateEvent.class);
            WalletUpdateRequest walletUpdateRequest = new WalletUpdateRequest(-walletUpdateEvent.getRequiredBalance(), -walletUpdateEvent.getRequiredBalance());
            this.walletService.updateWalletBalance(walletUpdateEvent.getUcc(), walletUpdateRequest);

            PortfolioUpdateRequest portfolioUpdateRequest  = PortfolioUpdateRequest.builder()
                    .symbol(walletUpdateEvent.getSymbol())
                    .quantity(walletUpdateEvent.getQuantity())
                    .price(walletUpdateEvent.getRequiredBalance())
                    .ucc(walletUpdateEvent.getUcc().toString())
                    .build();

            this.portfolioService.updatePortfolioAfterBidMatch(portfolioUpdateRequest);
            this.kafkaTemplate.send("wallet-validation-update", walletUpdateEvent.getMatchId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("EX: {}", e.getMessage());
            /* TODO: IMPLEMENT LOGGING! */
        }
    }


}
