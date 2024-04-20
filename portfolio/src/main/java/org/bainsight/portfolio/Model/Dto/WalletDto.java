package org.bainsight.portfolio.Model.Dto;


import jakarta.persistence.*;
import lombok.*;
import org.bainsight.portfolio.Model.Entity.Transaction;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDto {

    private Long walletId;

    private Double withdrawableBalance;
    private Double currentBalance;

    private List<TransactionDto> transactions;

}
