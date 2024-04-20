package org.bainsight.processing.Service;


import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.bainsight.*;
import org.bainsight.processing.Exception.RiskCheckFailureException;
import org.bainsight.processing.Model.Dto.OrderRequest;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderProcessingService {


    private RiskServiceGrpc.RiskServiceBlockingStub riskClient;


    public OrderProcessingService(@GrpcClient("risk-management") final RiskServiceGrpc.RiskServiceBlockingStub riskClient) {
        this.riskClient = riskClient;
    }


    public void checkIfRiskFreeElseThrow(OrderRequest orderRequest, final String ucc){
        try{
            RiskRequest riskRequest = RiskRequest.newBuilder()
                    .setQuantity(orderRequest.quantity())
                    .setSymbol(orderRequest.symbol())
                    .setTransactionType(orderRequest.transactionType())
                    .setPrice(orderRequest.price())
                    .setOrderType(orderRequest.orderType())
                    .setUcc(ucc)
                    .build();

            Proceedable proceedable = riskClient.checkIfProceedable(riskRequest);
            if(!proceedable.getProceedable()) throw new RiskCheckFailureException(proceedable.getMessage());
        }
        catch (StatusRuntimeException ex){
            throw new RiskCheckFailureException(ex.getMessage());
        }
    }


}
