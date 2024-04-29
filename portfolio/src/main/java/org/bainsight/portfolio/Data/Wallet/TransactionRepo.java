package org.bainsight.portfolio.Data.Wallet;

import org.bainsight.portfolio.Model.Entity.Transaction;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TransactionRepo extends PagingAndSortingRepository<Transaction, Long>, JpaRepository<Transaction, Long> {

    Optional<Page<Transaction>> findByWallet(Wallet wallet, PageRequest pageRequest);

    @Query("SELECT t from Transaction as t  where t.wallet.ucc = :uniqueClientCode order by t.timestamp desc")
    Optional<Page<Transaction>> findByUcc(UUID uniqueClientCode, PageRequest pageRequest);
}
