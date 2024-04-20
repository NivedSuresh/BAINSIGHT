package org.exchange.library.Dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Enums.Validity;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiskOrderResponse {
    private UUID orderId;
    private String ucc;
    private TransactionType transactionType;
    private OrderType orderType;
    private String symbol;
    private Long quantity;
    private Double price;
    private OrderStatus status;
    private OrderStatus exchangeOrderStatus;
    private Validity validity;
    private Double triggerPrice;
}
