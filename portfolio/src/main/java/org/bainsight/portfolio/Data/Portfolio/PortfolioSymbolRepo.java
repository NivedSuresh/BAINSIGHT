package org.bainsight.portfolio.Data.Portfolio;


import jakarta.persistence.LockModeType;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface PortfolioSymbolRepo extends JpaRepository<PortfolioSymbol, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT ps FROM PortfolioSymbol ps WHERE ps.portfolio.ucc = :ucc AND ps.symbol = :symbol")
    Optional<PortfolioSymbol> findByUccAndSymbolForUpdate(@Param("ucc") UUID ucc, @Param("symbol") String symbol);


    @Query("SELECT ps from PortfolioSymbol as ps where ps.portfolio.ucc = :ucc")
    Optional<List<PortfolioSymbol>> findByUcc(UUID ucc, PageRequest pageRequest);

    @Query("SELECT ps from PortfolioSymbol as ps where ps.portfolio.ucc = :ucc and ps.quantity > 0")
    Optional<Page<PortfolioSymbol>> findByUccAsPage(UUID ucc, PageRequest pageRequest);


    @Modifying
    @Query("UPDATE PortfolioSymbol ps set ps.openQuantity = 0 where ps.openQuantity > 0")
    void resetOpenOrderCount();

}

