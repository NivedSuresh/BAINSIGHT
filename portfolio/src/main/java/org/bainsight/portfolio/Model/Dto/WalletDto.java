package org.bainsight.portfolio.Model.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDto {

    private Double withdrawableBalance;
    private Double currentBalance;

    private PagedTransactions transactions;

}
