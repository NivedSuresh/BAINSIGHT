package org.bainsight.order.Model.Events;

import lombok.Builder;

import java.time.LocalDateTime;



@Builder
public record OrderMatch (
        String orderId,
        String ucc,
        String symbol,
        String exchange,
        LocalDateTime matchTime,
        long quantityRequested,
        long matchedQuantity,
        double priceMatchedFor
) {}
