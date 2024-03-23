package com.exchange.riskmanagement.Model.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("client_symbols")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ClientSymbol {
    @Id
    private Long id;
    private UUID ucc;
    private String symbol;
    private Long size;
}
