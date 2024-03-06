package org.exchange.order.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.Validity;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    @Id
    @Column("order_id")
    private UUID orderId;
    private UUID ucc;
    private String exchange;
    private TransactionType transactionType;
    @Column("order_type")
    private OrderType orderType;
    @Column("order_status")
    private OrderStatus status;
    private String symbol;
    private Long quantity;
    @Column("matched_size")
    private Long filledQuantity;
    private Double price;
    private LocalDateTime created;
    @Column("last_updated")
    private LocalDateTime last_updated;
    private Validity validity;
    List<Match> matches;
    List<OrderExchange> orderExchanges;
}
