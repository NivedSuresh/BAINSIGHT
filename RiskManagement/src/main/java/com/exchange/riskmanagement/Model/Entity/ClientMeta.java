package com.exchange.riskmanagement.Model.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("client")
public class ClientMeta {
    @Id
    private UUID ucc;
    private Double balance;
}
