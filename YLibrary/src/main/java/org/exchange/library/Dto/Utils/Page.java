package org.exchange.library.Dto.Utils;

import lombok.Builder;

@Builder
public record Page(short page, boolean next, boolean prev) {}
