package org.bainsight.portfolio;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bainsight.portfolio.Data.Portfolio.PortfolioService;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.bainsight.portfolio.Kafka.MatchUpdates;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.exchange.library.KafkaEvent.PortfolioUpdateEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Testcontainers
@SpringBootTest
/* Don't autoconfigure the database but use the test database provided */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebTestClient
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MatchTests {


    private static final UUID ucc = UUID.randomUUID();

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MatchUpdates matchUpdates;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private WalletService walletService;


    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @Test
    @Order(1)
    void testMatchBid() throws JsonProcessingException, ExecutionException, InterruptedException {

        Wallet wallet = this.walletService.fetchWallet(ucc.toString());

        WalletUpdateRequest updateRequest = new WalletUpdateRequest(10000.0, 0.0);
        this.walletService.updateWalletBalance(ucc, updateRequest);


        PortfolioUpdateEvent updateEvent = PortfolioUpdateEvent.builder()
                .isBid(true)
                .ucc(ucc)
                .symbol("AAPL")
                .pricePerShare(100.0)
                .quantity(70L)
                .build();


        this.matchUpdates.listenToMatches(mapper.writeValueAsString(updateEvent));

        PortfolioSymbol portfolioSymbol = this.portfolioService.fetchUserPortfolioSymbol(ucc.toString(), "AAPL");
        wallet = this.walletService.fetchWallet(ucc.toString());


        Assertions.assertEquals(wallet.getCurrentBalance(), 3000.0);


        Assertions.assertEquals(portfolioSymbol.getQuantity(), 70);



        updateEvent = PortfolioUpdateEvent.builder()
                .isBid(true)
                .ucc(ucc)
                .symbol("AAPL")
                .pricePerShare(100.0)
                .quantity(10L)
                .build();

        String valueAsString = mapper.writeValueAsString(updateEvent);

        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

        List<CompletableFuture<Void>> futures = new ArrayList<>();


        /* THIS WILL TEST THE RETRY MECHANISM IF THE LOCK IS NOT AVAILABLE */
        for(int i=0 ; i<3 ; i++){
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.matchUpdates.listenToMatches(valueAsString), service);
            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get();

        portfolioSymbol = this.portfolioService.fetchUserPortfolioSymbol(ucc.toString(), "AAPL");
        wallet = this.walletService.fetchWallet(ucc.toString());


        Assertions.assertEquals(wallet.getCurrentBalance(), 0.0);


        Assertions.assertEquals(portfolioSymbol.getQuantity(), 100);
        Assertions.assertEquals(portfolioSymbol.getQuantity(), 100);
        Assertions.assertEquals(portfolioSymbol.getInvestedAmount(), 10000);
        Assertions.assertEquals(portfolioSymbol.getSoldAmount(), 0);

        System.out.println(portfolioSymbol);

    }

    @Test
    @Order(2)
    void testAskMatch() throws JsonProcessingException, ExecutionException, InterruptedException {

        PortfolioUpdateRequest updateRequest = PortfolioUpdateRequest.builder()
                .ucc(ucc.toString())
                .symbol("AAPL")
                .quantity(100L)
                .build();

        this.portfolioService.updatePortfolioAfterAsk(updateRequest);

        Wallet wallet = this.walletService.fetchWallet(ucc.toString());
        Double prevBalance = wallet.getCurrentBalance();


        PortfolioUpdateEvent updateEvent = PortfolioUpdateEvent.builder()
                .isBid(false)
                .ucc(ucc)
                .symbol("AAPL")
                .pricePerShare(100.0)
                .quantity(25L)
                .build();

        String valueAsString = mapper.writeValueAsString(updateEvent);

        ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

        List<CompletableFuture<Void>> futures = new ArrayList<>();


        /* THIS WILL TEST THE RETRY MECHANISM IF THE LOCK IS NOT AVAILABLE */
        for(int i=0 ; i<4 ; i++){
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.matchUpdates.listenToMatches(valueAsString), service);
            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get();


        wallet = this.walletService.fetchWallet(ucc.toString());
        PortfolioSymbol portfolioSymbol = this.portfolioService.fetchUserPortfolioSymbol(ucc.toString(), "AAPL");


        Assertions.assertEquals(wallet.getCurrentBalance(), 10000);
        Assertions.assertEquals(wallet.getAvailableBalance(), 10000);

        Assertions.assertEquals(portfolioSymbol.getQuantity(), 0);
        Assertions.assertEquals(portfolioSymbol.getSoldAmount(), 10000);
        Assertions.assertEquals(portfolioSymbol.getInvestedAmount(), 10000);


    }



}
