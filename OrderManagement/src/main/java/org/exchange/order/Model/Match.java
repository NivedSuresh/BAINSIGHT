package org.exchange.order.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("matches")
public class Match {
    @Id
    @Column("match_id")
    private UUID matchId;
    @Column("order_id")
    private UUID orderId;
    @Column("exchange_match_id")
    private UUID exchangeMatchId;
    @Column("matched_order_id")
    private UUID matchedOrderId;
    @Column("matched_quantity")
    private Long matchedQuantity;
    @Column("matched_price")
    private Double matchedPrice;
    @Column("matched_broker_id")
    private String matchedBrokerId;
    @Column("execution_time")
    private LocalDateTime executionTime;
}
