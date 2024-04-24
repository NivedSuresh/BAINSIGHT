package org.exchange.library.KafkaEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletUpdateEvent {
    private UUID ucc;
    private long matchId;
    private double requiredBalance;
    private String symbol;
    private long quantity;
}
