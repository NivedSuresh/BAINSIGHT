package org.exchange.library.Advice;

import lombok.Builder;


@Builder
public record ErrorResponse(String message, String errorCode) {
}
