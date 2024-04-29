package org.bainsight.portfolio.Model.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.exchange.library.Dto.Utils.BainsightPage;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedTransactions {

    private List<TransactionDto> transactions;
    private BainsightPage page;

}
