package org.bainsight.portfolio.Model.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioSymbolDto {
    private Long symbolQuantityId;
    private String symbol;
    private Double investedAmount;
    private Double soldAmount;
    private Long quantity;
}
