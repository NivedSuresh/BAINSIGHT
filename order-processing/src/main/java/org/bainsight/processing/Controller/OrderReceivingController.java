package org.bainsight.processing.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.RiskRequest;
import org.bainsight.processing.Mapper.Mapper;
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
    private final Mapper mapper;

    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestHeader("x-auth-user-id") final String ucc,
                                           @RequestBody final OrderRequest orderRequest){
        RiskRequest riskRequest = this.mapper.getRiskRequest(orderRequest, ucc);
        this.processingService.checkIfRiskFreeElseThrow(riskRequest);

        this.processingService.placeOrder(orderRequest, ucc);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@RequestHeader("x-auth-user-id") final String ucc,
                                            @PathVariable final String orderId){
        this.processingService.cancelOrder(ucc, orderId);
        return ResponseEntity.ok().build();
    }

}