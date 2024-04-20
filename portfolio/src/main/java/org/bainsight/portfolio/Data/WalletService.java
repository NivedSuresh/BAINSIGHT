package org.bainsight.portfolio.Data;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Exceptions.WalletNotFoundException;
import org.bainsight.portfolio.Model.Dto.NewTransaction;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Transaction;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.library.Exception.Order.NotEnoughBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepo walletRepo;

    public Wallet fetchWallet(String ucc){

        UUID uniqueClientId = UUID.fromString(ucc);

        return this.walletRepo.findByUcc(uniqueClientId).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setTransactions(new ArrayList<>());
            wallet.setUcc(uniqueClientId);
            wallet.setCurrentBalance(0.0);
            wallet.setAvailableBalance(0.0);
            return this.walletRepo.save(wallet);
        });

    }



    @Transactional
    public void updateWalletBalance(final String ucc, WalletUpdateRequest updateRequest){
        UUID uniqueClientId = UUID.fromString(ucc);
        try{

            this.walletRepo.updateBalance(
                    updateRequest.changeInBalance(),
                    updateRequest.changeInAvailableBalance(),
                    uniqueClientId
            );
        }
        catch (ConstraintViolationException e){ throw new NotEnoughBalanceException(); }
    }


    @Transactional
    public void addNewTransaction(final NewTransaction newTransaction){

        try{

            Wallet wallet = this.walletRepo.findByUcc(UUID.fromString(newTransaction.getUcc())).orElseThrow(WalletNotFoundException::new);

            Transaction transaction = Transaction.builder()
                    .amount(newTransaction.getAmount())
                    .walletId(wallet.getWalletId())
                    .timestamp(LocalDateTime.now())
                    .build();

            wallet.setCurrentBalance(newTransaction.getAmount() + wallet.getCurrentBalance());
            wallet.setAvailableBalance(newTransaction.getAmount() + wallet.getAvailableBalance());

            wallet.getTransactions().add(transaction);
            this.walletRepo.save(wallet);
        }
        catch (RuntimeException e){
            if(e instanceof ConstraintViolationException) throw new NotEnoughBalanceException();
            else throw new ServiceUnavailableException();
        }
    }

}
