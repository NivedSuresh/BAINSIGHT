package org.bainsight.portfolio.Model.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(
        name = "portfolio_symbol",
        uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "symbol"})
)
public class PortfolioSymbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long symbolQuantityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    @ToString.Exclude
    private Portfolio portfolio;

    private String symbol;
    private Double investedAmount;
    private Double soldAmount;
    private Long quantity;
    private Long openQuantity;
}
