package org.bainsight.portfolio.Model.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.bainsight.portfolio.Model.Enums.WalletTransactionType;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private WalletTransactionType walletTransactionType;

    private Double amount;

    private Long walletId;

    private LocalDateTime timestamp;

}
