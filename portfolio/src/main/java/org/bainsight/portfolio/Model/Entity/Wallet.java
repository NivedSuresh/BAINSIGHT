package org.bainsight.portfolio.Model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @Column(unique = true)
    private UUID ucc;


    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0 CHECK (available_balance >= 0)")
    private Double availableBalance;

    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0 CHECK (current_balance >= 0)")
    private Double currentBalance;

    @OneToMany(fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               mappedBy = "walletId")
    @ToString.Exclude
    private List<Transaction> transactions;

}
