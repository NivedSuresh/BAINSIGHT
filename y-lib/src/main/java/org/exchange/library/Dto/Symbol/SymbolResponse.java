package org.exchange.library.Dto.Symbol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SymbolResponse {
    Long id;
    String symbol;
}
