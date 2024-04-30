package org.bainsight.order.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private UUID orderId;

    private String symbol;

    private String exchange;

    private TransactionType transactionType;

    private OrderType orderType;

    private Long quantityRequested;

    private Double priceRequestedFor;
    private Double totalAmountSpent;

    private Long quantityMatched;

    private LocalDateTime orderPlacedAt;

    private LocalDateTime lastUpdatedAt;

    private String orderStatus;
}
