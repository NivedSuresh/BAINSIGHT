package org.bainsight.order.Model.Events;

import lombok.*;
import org.bainsight.OrderType;
import org.bainsight.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;



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
