package org.bainsight.portfolio.Data;

import org.bainsight.portfolio.Model.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
interface WalletRepo extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUcc(UUID uuid);

    @Modifying
    @Query("update Wallet as w set w.currentBalance = w.currentBalance + :changeInBalance, w.availableBalance = w.availableBalance + :changeInAvailableBalance where w.ucc = :ucc")
    void updateBalance(double changeInBalance, double changeInAvailableBalance, UUID ucc);


}
