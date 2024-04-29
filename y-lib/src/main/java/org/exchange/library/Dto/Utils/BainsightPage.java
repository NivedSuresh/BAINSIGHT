package org.exchange.library.Dto.Utils;

import lombok.Builder;

@Builder
public record BainsightPage(short page, boolean next, boolean prev) {}
