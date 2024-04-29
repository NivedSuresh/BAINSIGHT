package org.bainsight.order.Model.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.exchange.library.Enums.MatchStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;
    private UUID orderId;
    private LocalDateTime matchTime;
    private long matchedQuantity;
    private double priceMatchedFor;
    private boolean wasValidated;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;
}
