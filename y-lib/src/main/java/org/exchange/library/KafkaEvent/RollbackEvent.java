package org.exchange.library.KafkaEvent;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RollbackEvent {
    private String ucc;
    private long quantity;
    private double price;
    private String symbol;
    private TransactionType transactionType;
    private OrderType orderType;
}
