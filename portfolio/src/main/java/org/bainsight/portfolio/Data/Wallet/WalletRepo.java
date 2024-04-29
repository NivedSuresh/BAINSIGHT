package org.bainsight.portfolio.Data.Wallet;

import jakarta.persistence.LockModeType;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
interface WalletRepo extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUcc(UUID uuid);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Wallet> findWithLockingByUcc(UUID ucc);


    @Modifying
    @Query("update Wallet as w set w.currentBalance = w.currentBalance + :changeInBalance, w.availableBalance = w.availableBalance + :changeInAvailableBalance where w.ucc = :ucc")
    void updateBalance(double changeInBalance, double changeInAvailableBalance, UUID ucc);

    @Query("select w.availableBalance from Wallet as w where w.ucc = :ucc")
    Optional<Double> findAvailableBalance(UUID ucc);

    @Modifying
    @Query("UPDATE  Wallet as w set w.availableBalance = w.currentBalance where w.availableBalance < w.currentBalance")
    void resetAvailableBalance();
}
