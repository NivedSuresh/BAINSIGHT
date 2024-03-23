package org.exchange.library.Dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Enums.OrderStatus;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private UUID ucc;
    private String broker_order_id;
    private TransactionType category;
    private OrderType type;
    private OrderStatus status;
    private String symbol;
    private Long requested_size;
    private Long matched_size;
    private Double price_per_symbol;
    private LocalDateTime created;
    private LocalDateTime last_updated;
}
