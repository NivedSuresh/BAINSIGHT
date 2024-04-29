package org.bainsight.portfolio.Model.Entity;

import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private WalletTransactionType walletTransactionType;

    private Double amount;

    @JoinColumn(name = "wallet_id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Wallet wallet;

    private LocalDateTime timestamp;

}
