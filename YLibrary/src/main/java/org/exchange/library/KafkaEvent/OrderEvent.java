package org.exchange.library.KafkaEvent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.Validity;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Enums.OrderType;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderEvent {
    private UUID orderId;
    private String ucc;
    private TransactionType transactionType;
    private OrderType orderType;
    private String symbol;
    private Long quantity;
    private Double price;
    private OrderStatus status;
    private Validity validity;
    private Double triggerPrice;
}
