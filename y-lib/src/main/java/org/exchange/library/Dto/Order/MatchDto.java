package org.exchange.library.Dto.Order;

import java.time.LocalDateTime;

public record MatchDto(
        Long quantity,
        LocalDateTime matchTime,
        String broker,
        String paymentUri
) {}
