package org.bainsight.portfolio.Data.Wallet;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bainsight.Proceedable;
import org.bainsight.ValidateBid;
import org.bainsight.portfolio.Debug.Debugger;
import org.bainsight.portfolio.Exceptions.WalletNotFoundException;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.NewTransaction;
import org.bainsight.portfolio.Model.Dto.PagedTransactions;
import org.bainsight.portfolio.Model.Dto.TransactionDto;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Transaction;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.bainsight.portfolio.Model.Enums.WalletTransactionType;
import org.exchange.library.Dto.Utils.BainsightPage;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Exception.GlobalException;
import org.exchange.library.Exception.IO.ServiceUnavailableException;
import org.exchange.library.Exception.Order.NotEnoughBalanceException;
import org.exchange.library.KafkaEvent.RollbackEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepo walletRepo;
    private final TransactionRepo transactionRepo;
    private final Debugger DEBUGGER;
    private final ExecutorService greenExecutor;
    private final Mapper mapper;


    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet fetchWallet(String ucc){

        UUID uniqueClientId = UUID.fromString(ucc);

        return this.walletRepo.findWithLockingByUcc(uniqueClientId).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setTransactions(new ArrayList<>());
            wallet.setUcc(uniqueClientId);

//            wallet.setCurrentBalance(0.0);
//            wallet.setAvailableBalance(0.0);

            {
                /* TODO: SHOULD BE COMMENTED BEFORE EVOKING TESTS TESTING
                AND THE ABOVE SHOULD BE UNCOMMENTED BEFORE DEPLOYING */
                wallet.setCurrentBalance(100000.0);
                wallet.setAvailableBalance(100000.0);
            }

            wallet.setVersion(1L);
            return this.walletRepo.save(wallet);
        });

    }






    @Transactional(propagation = Propagation.REQUIRED)
    public void updateWalletBalance(final UUID uniqueClientId, WalletUpdateRequest updateRequest){
        try{
            final Wallet wallet = this.fetchWallet(uniqueClientId.toString());

            double current = wallet.getCurrentBalance() + updateRequest.changeInBalance();
            if(current < 0) throw new NotEnoughBalanceException();

            double available = wallet.getAvailableBalance() + updateRequest.changeInAvailableBalance();
            if(available < 0) throw new NotEnoughBalanceException();

            wallet.setCurrentBalance(current);
            wallet.setAvailableBalance(available);


            this.greenExecutor.execute(() -> this.persistTransactionIfRequired(updateRequest.changeInBalance(), wallet));


            DEBUGGER.DEBUG(log,"Wallet before update: {}", wallet);
            DEBUGGER.DEBUG(log, "Wallet after update: {}", this.walletRepo.save(wallet));
        }
        catch (RuntimeException e)
        {
            log.error(e.getMessage());
            if(e instanceof ObjectOptimisticLockingFailureException)
            {
                throw e;
            }
            if(e instanceof GlobalException) throw e;
            throw new ServiceUnavailableException();
        }
    }

    private void persistTransactionIfRequired(Double transactionAmount, Wallet wallet) {
        if(transactionAmount == 0) return;
        try{
            Transaction transaction = Transaction.builder()
                    .timestamp(LocalDateTime.now())
                    .walletTransactionType(transactionAmount < 0 ? WalletTransactionType.DEBIT : WalletTransactionType.CREDIT)
                    .wallet(wallet)
                    .amount(transactionAmount)
                    .build();

            this.transactionRepo.save(transaction);
        }
        catch (Exception e){
            /* TODO: IMPLEMENT LOGGING */
            log.error("Failed while persisting transaction. {}", e.getMessage());
        }
    }


    @Transactional
    public void addNewTransaction(final NewTransaction newTransaction){
        try
        {
            Wallet wallet = this.walletRepo.findByUcc(UUID.fromString(newTransaction.getUcc())).orElseThrow(WalletNotFoundException::new);

            Transaction transaction = Transaction.builder()
                    .amount(newTransaction.getAmount())
                    .wallet(wallet)
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
        this.updateWalletBalance(UUID.fromString(request.getUcc()), new WalletUpdateRequest(0.0, request.getPrice() * request.getQuantity()));
    }

    @Transactional
    public Proceedable validateBalance(ValidateBid request) {
        Wallet wallet = this.fetchWallet(request.getUcc());
        if(wallet.getAvailableBalance() < request.getBalanceRequired()) return Proceedable.newBuilder().setMessage("Not enough balance in wallet!").setProceedable(false).build();
        return Proceedable.newBuilder().setProceedable(true).setMessage("").build();
    }


    @Transactional
    public void resetAvailableBalance() {
        this.walletRepo.resetAvailableBalance();
    }

    public PagedTransactions fetchTransactions(UUID uniqueClientCode, Integer pageNumber) {
        if(pageNumber == null || pageNumber < 0) pageNumber = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 5);
        Optional<Page<Transaction>> optional = this.transactionRepo.findByUcc(uniqueClientCode, pageRequest);

        if(optional.isEmpty())
        {
            log.info("No Transactions found!");
            BainsightPage page = new BainsightPage(pageNumber.shortValue(), false, pageNumber > 1);
            return new PagedTransactions(List.of(), page);
        }

        Page<Transaction> transactionPage = optional.get();

        List<TransactionDto> transactions = transactionPage.getContent().stream().map(this.mapper::toTransactionDto).toList();
        BainsightPage page = new BainsightPage(pageNumber.shortValue(), transactionPage.hasNext(), transactionPage.hasPrevious());


        return new PagedTransactions(transactions, page);
    }
}
