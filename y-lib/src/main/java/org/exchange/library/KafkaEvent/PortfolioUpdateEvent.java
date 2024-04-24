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
public class PortfolioUpdateEvent {
    private String symbol;
    private UUID ucc;
    private Long quantity;
    private Double pricePerShare;
    private boolean isBid;
}
