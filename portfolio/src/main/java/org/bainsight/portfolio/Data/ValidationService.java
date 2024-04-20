package org.bainsight.portfolio.Data;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.bainsight.PortfolioValidateGrpc;
import org.bainsight.Proceedable;
import org.bainsight.ValidateAsk;
import org.bainsight.ValidateBid;
import org.bainsight.portfolio.Model.Dto.PortfolioUpdateRequest;
import org.bainsight.portfolio.Model.Dto.WalletUpdateRequest;
import org.bainsight.portfolio.Model.Entity.PortfolioSymbol;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Exception.GlobalException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@GrpcService
@RequiredArgsConstructor
public class ValidationService extends PortfolioValidateGrpc.PortfolioValidateImplBase {

    private final PortfolioService portfolioService;
    private final WalletService walletService;


    @Override
    public void checkIfBidValid(ValidateBid request, StreamObserver<Proceedable> responseObserver) {
        WalletUpdateRequest updateRequest = new WalletUpdateRequest(0.0, -request.getBalanceRequired());
        Proceedable proceedable;
        try{
            this.walletService.updateWalletBalance(request.getUcc(), updateRequest);
            proceedable = getProceedable("", true);
        }
        catch (GlobalException ex){
            proceedable = getProceedable(ex.getMessage(), false);
        }
        this.publishAndComplete(proceedable, responseObserver);
    }



    @Override
    public void checkIfAskValid(ValidateAsk request, StreamObserver<Proceedable> responseObserver) {

        PortfolioUpdateRequest updateRequest = PortfolioUpdateRequest.builder().quantity(request.getQuantityRequired()).symbol(request.getSymbol()).ucc(request.getUcc())
                .price(0.0).build();

        Proceedable proceedable;

        try
        {
            this.portfolioService.updatePortfolioAfterAsk(updateRequest);
            proceedable = getProceedable("", true);
            this.publishAndComplete(proceedable, responseObserver);
        }
        catch (RuntimeException e)
        {
            String message = e instanceof GlobalException ? e.getMessage() : "Service Unavailable";
            proceedable = getProceedable(message, false);
            this.publishAndComplete(proceedable, responseObserver);
        }

    }


    private Proceedable getProceedable(String message, boolean proceedable){
        return Proceedable.newBuilder().setMessage(message).setProceedable(proceedable).build();
    }

    private void publishAndComplete(Proceedable proceedable, StreamObserver<Proceedable> responseObserver){
        responseObserver.onNext(proceedable);
        responseObserver.onCompleted();
    }
}
