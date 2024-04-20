package org.bainsight.portfolio.Model.Dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;

import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PortfolioDto {
    private Long portfolioId;
    private UUID ucc;
    List<PortfolioSymbolDto> portfolioSymbols;
}
