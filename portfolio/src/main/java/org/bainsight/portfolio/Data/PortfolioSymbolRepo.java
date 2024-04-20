package org.bainsight.portfolio.Data;


import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PortfolioSymbolRepo extends JpaRepository<PortfolioSymbol, Long> {

}
