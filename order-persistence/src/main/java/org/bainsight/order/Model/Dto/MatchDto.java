package org.bainsight.order.Model.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchDto(

        LocalDateTime matchTime,
        long matchedQuantity,
        double priceMatchedFor
) {}
