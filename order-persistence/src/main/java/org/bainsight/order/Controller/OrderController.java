package org.bainsight.order.Controller;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.bainsight.order.Data.OrderPersistance.GrpcOrderService;
import org.bainsight.order.Model.Dto.PageableOrders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bainsight/persistence")
public class OrderController {


    private final GrpcOrderService orderService;

    @CircuitBreaker(name = "order-persistence")
    @Retry(name = "order-persistence")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableOrders fetchOrders(@RequestHeader("x-auth-user-id") String ucc,
                                      @RequestParam(value = "page", required = false) Integer page){
        if(page == null) page = 1;
        return this.orderService.findOrdersByPageAndUccWithPage(UUID.fromString(ucc), page);
    }

}
