package org.bainsight.portfolio;


import io.grpc.internal.testing.StreamRecorder;
import org.bainsight.Proceedable;
import org.bainsight.ValidateAsk;
import org.bainsight.portfolio.Data.PortfolioService;
import org.bainsight.portfolio.Data.ValidationService;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.exchange.library.Enums.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
//Don't autoconfigure the database but use the test database provided
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ValidationTests {


    @Autowired ValidationService validationService;
    @Autowired PortfolioService portfolioService;


    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @Test
    void connectionEstablished() {
        postgres.start();
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }


//    @Test
//    void testCheckIfAskValid() throws ExecutionException, InterruptedException {
//
//        String ucc = UUID.randomUUID().toString();
//
//        PortfolioUpdateRequest updateRequest = PortfolioUpdateRequest.builder()
//                .price(100.0)
//                .symbol("AAPL")
//                .transactionType(TransactionType.BID)
//                .quantity(100L)
//                .build();
//
//        this.portfolioService.updatePortfolio(ucc, updateRequest);
//
//
//        ValidateAsk validateAsk1 = ValidateAsk.newBuilder()
//                .setSymbol("AAPL")
//                .setUcc(ucc)
//                .setQuantityRequired(100)
//                .build();
//
//        StreamRecorder<Proceedable> responseObserver = StreamRecorder.create();
//
//        this.validationService.checkIfAskValid(validateAsk1, responseObserver);
//        Proceedable proceedable = responseObserver.firstValue().get();
//
//        Assertions.assertTrue(proceedable.getProceedable());
//
//        ValidateAsk validateAsk2 = ValidateAsk.newBuilder()
//                .setSymbol("AAPL")
//                .setUcc(ucc)
//                .setQuantityRequired(101)
//                .build();
//
//        this.validationService.checkIfAskValid(validateAsk2, responseObserver);
//        proceedable = responseObserver.getValues().get(1);
//        Assertions.assertFalse(proceedable.getProceedable());
//    }



}
