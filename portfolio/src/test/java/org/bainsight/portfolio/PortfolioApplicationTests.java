package org.bainsight.portfolio;

import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@Testcontainers
@SpringBootTest
/* Don't autoconfigure the database but use the test database provided */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebTestClient
class PortfolioApplicationTests {

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private WalletService walletService;

    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void testPortfolio(){

        final String ucc = UUID.randomUUID().toString();
        final String symbol = "AAPL";

        /* WILL CREATE AND RETURN A PORTFOLIO */
        Portfolio portfolio = portfolioService.fetchUserPortfolio(ucc);

        PortfolioUpdateRequest updateRequest = PortfolioUpdateRequest.builder()
                .ucc(ucc)
                .price(120.0)
                .quantity(10L)
                .symbol(symbol)
                .build();

        portfolioService.updatePortfolioAfterBidMatch(updateRequest);

        PortfolioSymbol portfolioSymbol = portfolioService.fetchUserPortfolioSymbol(ucc, symbol);

        Assertions.assertEquals(portfolioSymbol.getSymbol(), symbol);
        Assertions.assertEquals(portfolioSymbol.getInvestedAmount(), 120);
        Assertions.assertEquals(portfolioSymbol.getSoldAmount(), 0);


        updateRequest = PortfolioUpdateRequest.builder()
                .price(100.0)
                .ucc(ucc)
                .quantity(8L)
                .symbol(symbol)
                .build();


        portfolioService.updatePortfolioAfterAsk(updateRequest);
        portfolioSymbol = this.portfolioService.fetchUserPortfolioSymbol(ucc, symbol);


        Assertions.assertEquals(portfolioSymbol.getSymbol(), symbol);
        Assertions.assertEquals(portfolioSymbol.getInvestedAmount(), 120);
        Assertions.assertEquals(portfolioSymbol.getOpenQuantity(), 8L);
        Assertions.assertEquals(portfolioSymbol.getQuantity(), 10L);


        updateRequest = PortfolioUpdateRequest.builder()
                .price(50.0)
                .ucc(ucc)
                .quantity(4L)
                .symbol(symbol)
                .build();

        portfolioService.updatePortfolioAfterAskMatch(updateRequest);
        portfolioSymbol = this.portfolioService.fetchUserPortfolioSymbol(ucc, symbol);

        Assertions.assertEquals(portfolioSymbol.getInvestedAmount(), 120);
        Assertions.assertEquals(portfolioSymbol.getOpenQuantity(), 4L);
        Assertions.assertEquals(portfolioSymbol.getSoldAmount(), 50);
        Assertions.assertEquals(portfolioSymbol.getQuantity(), 6L);
        Assertions.assertEquals(portfolioSymbol.getInvestedAmount(), 120);

    }




}
