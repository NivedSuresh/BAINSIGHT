package org.bainsight.portfolio.Data;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.bainsight.*;
import org.bainsight.portfolio.Debug.Debugger;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.exchange.library.Exception.GlobalException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ValidationService extends PortfolioValidateGrpc.PortfolioValidateImplBase {

    private final PortfolioService portfolioService;
    private final WalletService walletService;
    private final Debugger DEBUGGER;



    @Override
    public void checkIfBidValid(ValidateBid request, StreamObserver<Proceedable> responseObserver) {

        DEBUGGER.DEBUG(log, "Validating BID!");


        /* IF MARKET ORDER THEN DON'T DEDUCT BUT VALIDATE */
        if(request.getOrderType() == OrderType.ORDER_TYPE_MARKET){
            Proceedable proceedable = this.walletService.validateBalance(request);
            this.publishAndComplete(proceedable, responseObserver);
            return;
        }

        WalletUpdateRequest updateRequest = new WalletUpdateRequest(0.0, -request.getBalanceRequired());
        DEBUGGER.DEBUG(log, "Wallet Update Request: {}", updateRequest);

        Proceedable proceedable;
        try{
            UUID ucc = UUID.fromString(request.getUcc());
            this.walletService.updateWalletBalance(ucc, updateRequest, 1);
            DEBUGGER.DEBUG(log, "Balance validated and updated!");
            proceedable = getProceedable("", true);
        }
        catch (Exception ex){
            ex.printStackTrace();
            DEBUGGER.DEBUG(log, "Exception while validating balance: {}", ex.getMessage());
            proceedable = getProceedable(ex.getMessage(), false);
        }
        DEBUGGER.DEBUG(log, "Finished validating!");
        this.publishAndComplete(proceedable, responseObserver);
    }



    @Override
    public void checkIfAskValid(ValidateAsk request, StreamObserver<Proceedable> responseObserver) {

        PortfolioUpdateRequest updateRequest = PortfolioUpdateRequest.builder().quantity(request.getQuantityRequired()).symbol(request.getSymbol()).ucc(request.getUcc())
                .price(0.0).build();

        DEBUGGER.DEBUG(log, "Checking if ask valid: {}", updateRequest);

        Proceedable proceedable;

        try
        {
            this.portfolioService.updatePortfolioAfterAsk(updateRequest);
            DEBUGGER.DEBUG(log, "ASK VALID!");
            proceedable = getProceedable("", true);
        }
        catch (RuntimeException e)
        {
            DEBUGGER.DEBUG(log, "Exception caught while checking if ask valid! - {}", e.getMessage());
            String message = e instanceof GlobalException ? e.getMessage() : "Service Unavailable";
            proceedable = getProceedable(message, false);
        }

        this.publishAndComplete(proceedable, responseObserver);
    }


    private Proceedable getProceedable(String message, boolean proceedable){
        return Proceedable.newBuilder().setMessage(message).setProceedable(proceedable).build();
    }

    private void publishAndComplete(Proceedable proceedable, StreamObserver<Proceedable> responseObserver){
        System.out.println(proceedable);
        responseObserver.onNext(proceedable);
        responseObserver.onCompleted();
    }
}
