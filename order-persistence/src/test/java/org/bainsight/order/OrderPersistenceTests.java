package org.bainsight.order;

import io.grpc.internal.testing.StreamRecorder;
import org.bainsight.*;
import org.bainsight.order.GrpcService.OrderPersistance.GrpcOrderService;
import org.bainsight.order.Model.Entity.Order;
import org.bainsight.order.Model.Events.OrderMatch;
import org.exchange.library.Enums.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
/* Don't autoconfigure the database but use the test database provided */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderPersistenceTests {


    @Autowired
    GrpcOrderService grpcOrderService;

    private final long totalOrderQuantity = 100;
    private final long matched = 20;
    private final String ucc = UUID.randomUUID().toString();

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @Test
    @org.junit.jupiter.api.Order(1)
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }


    @Test
    @org.junit.jupiter.api.Order(2)
    void testGrpcOrderService(){


        final String symbol = "AAPL";


        GrpcOrderRequest orderRequest = GrpcOrderRequest.newBuilder()
                .setSymbol(symbol)
                .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                .setTransactionType(TransactionType.BID)
                .setPrice(0)
                .setQuantity(totalOrderQuantity)
                .setUcc(ucc)
                .setExchange("NSE")
                .build();


        StreamRecorder<OrderUID> responseObserver = StreamRecorder.create();
        grpcOrderService.persistOrder(orderRequest, responseObserver);

        OrderUID orderUID = responseObserver.getValues().getFirst();
        Order order = this.grpcOrderService.findOrderById(UUID.fromString(orderUID.getOrderId()));
        Assertions.assertEquals(order.getOrderId().toString(), orderUID.getOrderId());
    }



    @Test
    @org.junit.jupiter.api.Order(3)
    void testMatch(){

        List<Order> orders = this.grpcOrderService.findAllByOrderTypeAndOrderStatus(OrderType.ORDER_TYPE_LIMIT, OrderStatus.OPEN);
        Order order = orders.getFirst();

        OrderMatch orderMatch = OrderMatch.builder()
                .quantityRequested(totalOrderQuantity)
                .orderId(order.getOrderId().toString())
                .priceMatchedFor(101)
                .matchTime(LocalDateTime.now())
                .matchedQuantity(matched)
                .exchange("NSE")
                .symbol("AAPL")
                .ucc(ucc)
                .build();

        this.grpcOrderService.processMatch(orderMatch);

        order = this.grpcOrderService.findOrderById(order.getOrderId());

        Assertions.assertEquals(this.matched, order.getQuantityMatched());
        Assertions.assertEquals(order.getTotalAmountSpent(), 101 * order.getQuantityMatched());
    }




    @Test
    @org.junit.jupiter.api.Order(4)
    void fetchThatOrderAndCancel(){
        List<Order> orders = this.grpcOrderService.findAllByOrderTypeAndOrderStatus(OrderType.ORDER_TYPE_LIMIT, OrderStatus.OPEN);
        Order order = orders.getFirst();

        StreamRecorder<RiskRequest> responseObserver = StreamRecorder.create();
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setOrderStatus(OrderStatus.CANCELLED.name())
                .setOrderId(order.getOrderId().toString())
                .setUcc(order.getUcc().toString())
                .build();
        this.grpcOrderService.cancelOrder(request, responseObserver);

        RiskRequest riskRequest = responseObserver.getValues().getFirst();

        order = this.grpcOrderService.findOrderById(order.getOrderId());


        Assertions.assertEquals((this.totalOrderQuantity - this.matched), riskRequest.getQuantity());
        Assertions.assertEquals(order.getOrderStatus(), OrderStatus.CANCELLED.name());
        Assertions.assertEquals(order.getTotalAmountSpent(), matched * 101);
    }







}
