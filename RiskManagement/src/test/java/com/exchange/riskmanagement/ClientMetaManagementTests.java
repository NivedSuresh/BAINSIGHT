package com.exchange.riskmanagement;

import com.exchange.riskmanagement.Repository.ClientSecurityRepo;
import com.exchange.riskmanagement.Service.KafkaService;
import com.exchange.riskmanagement.Service.OrderService;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Enums.OrderType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;


@Testcontainers
@SpringBootTest
//Don't autoconfigure the database but use the test database provided
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientMetaManagementTests {

    @MockBean
    ClientSecurityRepo clientSecurityRepo;

    @Autowired
    OrderService orderService;


    @MockBean
    KafkaService kafkaService;

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));


    @Test
    void testPlaceAskOrder() {
        postgreSQLContainer.start();

        OrderRequest request = OrderRequest.builder()
                .quantity(20L)
                .orderType(OrderType.ORDER_TYPE_LIMIT)
                .price(100.0)
                .symbol("AAPL")
                .transactionType(TransactionType.ASK)
                .build();

        Mockito.when(clientSecurityRepo.findByUccAndSymbol("niveds406@gmail.com", request.symbol())).thenReturn(Mono.just(1000L));


    }

}
