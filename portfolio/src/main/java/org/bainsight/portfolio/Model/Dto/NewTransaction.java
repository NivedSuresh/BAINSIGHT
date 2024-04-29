package org.bainsight.portfolio.Model.Dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bainsight.portfolio.Model.Enums.WalletTransactionType;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewTransaction {

    private String ucc;

    private WalletTransactionType walletTransactionType;

    private Double amount;



}
