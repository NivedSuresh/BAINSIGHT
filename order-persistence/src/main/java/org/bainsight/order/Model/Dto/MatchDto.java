package org.bainsight.order.Model.Dto;

import java.time.LocalDateTime;

public record MatchDto(

        LocalDateTime matchTime,
        long matchedQuantity,
        double priceMatchedFor
) {}
