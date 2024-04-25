package org.bainsight.order.Model.Entity;


import jakarta.persistence.*;
import lombok.*;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(
        name = "orders", indexes = {
        @Index(name = "idx_order_ucc", columnList = "ucc"),
        @Index(name = "idx_order_order_placed_at", columnList = "order_placed_at")
})
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    private UUID ucc;

    private String symbol;

    private String exchange;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private Long quantityRequested;

    private Double priceRequestedFor;
    private Double totalAmountSpent;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long quantityMatched;

    @Column(updatable = false)
    private LocalDateTime orderPlacedAt;

    private LocalDateTime lastUpdatedAt;

    private String orderStatus;

    @Version
    private Long version;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderId")
    @ToString.Exclude
    private List<Match> matches;

}
