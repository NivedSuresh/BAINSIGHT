package com.exchange.riskmanagement.Model.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("symbol_meta")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SymbolMeta {
    private Long id;
    private String tradingSymbol;
    private Boolean tradable;
    private String exchange;
}
