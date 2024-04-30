package org.bainsight.portfolio.Controller;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.Wallet.WalletService;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.PagedTransactions;
import org.bainsight.portfolio.Model.Dto.WalletDto;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/bainsight/wallet")
@RequiredArgsConstructor
@RestController
@CircuitBreaker(name = "portfolio-service")
@Retry(name = "portfolio-service")
public class WalletController {

    private final WalletService walletService;
    private final Mapper mapper;



    /* TODO: TEST*/
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public WalletDto fetchUserWallet(@RequestHeader("x-auth-user-id") String ucc,
                                     @RequestParam(value = "page", required = false) Integer page){

        if(page == 1) throw new RuntimeException();
        Wallet wallet = this.walletService.fetchWallet(ucc);
        PagedTransactions transactions = this.walletService.fetchTransactions(wallet.getUcc(), page);

        return this.mapper.walletEntityToDto(wallet, transactions);
    }



    @GetMapping("/transactions")
    public PagedTransactions fetchTransactions(@RequestHeader("x-auth-user-id") String ucc,
                                               @RequestParam(value = "page", required = false) Integer page){
        if(page == 1) throw new RuntimeException();
        return this.walletService.fetchTransactions(UUID.fromString(ucc), page);
    }


}
