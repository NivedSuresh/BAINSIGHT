package org.bainsight.portfolio.Data.Portfolio;


import jakarta.persistence.LockModeType;
import org.bainsight.portfolio.Model.Entity.Portfolio;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface PortfolioRepo extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUcc(UUID ucc);


    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT ps FROM PortfolioSymbol ps JOIN ps.portfolio p WHERE p.ucc = :ucc AND ps.symbol = :symbol")
    Optional<PortfolioSymbol> fetchPortfolioSymbol(@Param("ucc") UUID ucc, String symbol);

}
