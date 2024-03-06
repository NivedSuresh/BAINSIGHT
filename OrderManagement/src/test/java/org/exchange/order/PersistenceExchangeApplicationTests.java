package org.exchange.order;

import org.exchange.order.Controller.AdminOrderController;
import org.exchange.order.Repository.OrderRepo;
import org.exchange.order.Service.PersistentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
class PersistenceExchangeApplicationTests {

    @Autowired
    PersistentService persistentService;


    @Autowired
    OrderRepo orderRepo;


    @Autowired
    AdminOrderController adminOrderController;

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));


}
