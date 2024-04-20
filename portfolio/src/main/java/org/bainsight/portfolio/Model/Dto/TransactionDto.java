package org.bainsight.portfolio.Model.Dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bainsight.portfolio.Model.Entity.Transaction;
import org.bainsight.portfolio.Model.Enums.WalletTransactionType;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
    private Long id;
    private WalletTransactionType walletTransactionType;
    private Double amount;
    private LocalDateTime timestamp;
}
