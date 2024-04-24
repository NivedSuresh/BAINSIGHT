package org.bainsight.portfolio.Data;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.Proceedable;
import org.bainsight.ValidateBid;
import org.bainsight.portfolio.Debug.Debugger;
import org.bainsight.portfolio.Exceptions.WalletNotFoundException;
import org.bainsight.portfolio.Model.Dto.NewTransaction;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Transaction;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.library.Exception.Order.NotEnoughBalanceException;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepo walletRepo;
    private final Debugger BEBUGGER;


    public Wallet fetchWallet(String ucc){

        UUID uniqueClientId = UUID.fromString(ucc);

        return this.walletRepo.findByUcc(uniqueClientId).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setTransactions(new ArrayList<>());
            wallet.setUcc(uniqueClientId);
            wallet.setCurrentBalance(0.0);
            wallet.setAvailableBalance(0.0);
            wallet.setVersion(1L);
            return this.walletRepo.save(wallet);
        });

    }



    @Transactional
    public void updateWalletBalance(final UUID uniqueClientId, WalletUpdateRequest updateRequest, int tryCount){
        try{
            Optional<Wallet> optional = this.walletRepo.findWithLockingByUcc(uniqueClientId);

            if(optional.isEmpty()){
                throw new WalletNotFoundException();
            }

            Wallet wallet = optional.get();

            double current = wallet.getCurrentBalance() + updateRequest.changeInBalance();
            if(current < 0) throw new NotEnoughBalanceException();

            double available = wallet.getAvailableBalance() + updateRequest.changeInAvailableBalance();
            if(available < 0) throw new NotEnoughBalanceException();

            wallet.setCurrentBalance(current);
            wallet.setAvailableBalance(available);
            long v = Objects.requireNonNullElse(wallet.getVersion(), 0L);
            wallet.setVersion(v + 1);


            BEBUGGER.DEBUG(log,"Wallet before update: {}", wallet);
            wallet = this.walletRepo.save(wallet);
            BEBUGGER.DEBUG(log, "Wallet after update: {}", wallet);
        }
        catch (RuntimeException e)
        {
            log.error(e.getMessage());
            if(e instanceof ObjectOptimisticLockingFailureException && tryCount <= 3)
            {
                this.sleep(500);
                this.updateWalletBalance(uniqueClientId, updateRequest, tryCount + 1);
                return;
            }
            if(e instanceof GlobalException) throw e;
            throw new ServiceUnavailableException();
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }


    @Transactional
    public void addNewTransaction(final NewTransaction newTransaction){
        try
        {
            Wallet wallet = this.walletRepo.findByUcc(UUID.fromString(newTransaction.getUcc())).orElseThrow(WalletNotFoundException::new);

            Transaction transaction = Transaction.builder()
                    .amount(newTransaction.getAmount())
                    .walletId(wallet.getWalletId())
                    .timestamp(LocalDateTime.now())
                    .build();

            wallet.setCurrentBalance(newTransaction.getAmount() + wallet.getCurrentBalance());
            wallet.setAvailableBalance(newTransaction.getAmount() + wallet.getAvailableBalance());
            wallet.setVersion(wallet.getVersion() + 1);

            wallet.getTransactions().add(transaction);
            this.walletRepo.save(wallet);
        }
        catch (RuntimeException e)
        {
            if(e instanceof ConstraintViolationException) throw new NotEnoughBalanceException();
            if(e instanceof WalletNotFoundException) throw e;
            else throw new ServiceUnavailableException();
        }
    }

    @Transactional
    public void rollbackWalletValidation(RollbackEvent request) {
        if(request.getOrderType() == OrderType.ORDER_TYPE_MARKET) return;
        this.updateWalletBalance(UUID.fromString(request.getUcc()), new WalletUpdateRequest(0.0, request.getPrice() * request.getQuantity()), 1);
    }

    public Proceedable validateBalance(ValidateBid request) {
        Optional<Double> optional = this.walletRepo.findAvailableBalance(UUID.fromString(request.getUcc()));
        if(optional.isEmpty() || optional.get() < request.getBalanceRequired()) return Proceedable.newBuilder().setMessage("Not enough balance in wallet!").setProceedable(false).build();
        return Proceedable.newBuilder().setProceedable(true).setMessage("").build();
    }

}
