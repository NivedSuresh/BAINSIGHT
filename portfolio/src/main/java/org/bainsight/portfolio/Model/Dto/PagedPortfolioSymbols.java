package org.bainsight.portfolio.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.exchange.library.Dto.Utils.BainsightPage;

import java.util.List;


@AllArgsConstructor
@Builder
@Data
public class PagedPortfolioSymbols {
    private List<PortfolioSymbolDto> portfolioSymbols;
    private BainsightPage page;
}
