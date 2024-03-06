package com.exchange.riskmanagement.Controller;


import com.exchange.riskmanagement.Service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.exchange.library.Dto.Order.OrderRequest;
import org.exchange.library.Dto.Order.RiskOrderResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bainsight/risk")
public class OrderController {

    private final OrderService orderService;


    /* The x-auth-user-id is appended from the gateway filter thus making
       the 'principal.name' property available in each and every service */
    @PostMapping("/order")
    public ResponseEntity<Mono<? extends RiskOrderResponse>> placeOrder(@Validated @RequestBody OrderRequest orderRequest,
                                                              @RequestHeader("x-auth-user-id") String ucc) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequest, ucc));
    }


}
