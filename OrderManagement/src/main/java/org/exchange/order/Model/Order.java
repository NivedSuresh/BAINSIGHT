package org.exchange.order.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Enums.Validity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {
    @Id
    @Column("order_id")
    private UUID orderId;
    private UUID ucc;
    @Column("transactionType")
    private TransactionType transactionType;
    @Column("order_type")
    private OrderType orderType;
    @Column("order_status")
    private OrderStatus orderStatus;
    @Column("exchange_order_status")
    private OrderStatus exchangeOrderStatus;
    private String symbol;
    private Long quantity;
    @Column("matched_size")
    private Long filledQuantity;
    @Column("executed_price")
    private Double executedPrice;
    @Column("total_price")
    private Double totalPrice;
    private LocalDateTime created;
    @Column("last_updated")
    private LocalDateTime last_updated;
    private Validity validity;
}
