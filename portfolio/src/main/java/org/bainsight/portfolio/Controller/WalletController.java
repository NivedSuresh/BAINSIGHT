package org.bainsight.portfolio.Controller;


import lombok.RequiredArgsConstructor;
import org.bainsight.portfolio.Data.WalletService;
import org.bainsight.portfolio.Mapper.Mapper;
import org.bainsight.portfolio.Model.Dto.WalletDto;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/bainsight/wallet")
@RequiredArgsConstructor
@RestController
public class WalletController {

    private final WalletService walletService;
    private final Mapper mapper;


    /* TODO: TEST*/
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public WalletDto fetchUserWallet(@RequestHeader("x-auth-user-id") String ucc){
        Wallet wallet = this.walletService.fetchWallet(ucc);
        return this.mapper.walletEntityToDto(wallet, false);
    }

//    /* TODO: TEST */
//    public void updateWalletBalance(@RequestHeader("x-auth-user-id") final String ucc,
//                                    final WalletUpdateRequest walletUpdateRequest){
//
//        this.walletService.updateWalletBalance(ucc, walletUpdateRequest);
//
//    }

}
