package org.bainsight.processing.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.processing.Model.Dto.OrderRequest;
import org.bainsight.processing.Service.OrderProcessingService;
import org.exchange.library.Dto.Order.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("api/bainsight/order")
@RestController
@RequiredArgsConstructor
public class OrderReceivingController {

    private final OrderProcessingService processingService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody final OrderRequest orderRequest,
                                                    @RequestHeader("x-auth-user-id") final String ucc){
        this.processingService.checkIfRiskFreeElseThrow(orderRequest, ucc);

        return ResponseEntity.accepted().body(OrderResponse.builder().brokerOrderId(UUID.randomUUID().toString()).build());
    }

}
