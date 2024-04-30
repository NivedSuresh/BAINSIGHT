package com.bainsight.risk;


import com.bainsight.risk.Data.CandleStickRepo;
import com.bainsight.risk.Data.DailyOrderMetaRepo;
import com.bainsight.risk.Message.gRPC_Client.RiskManagementService;
import com.bainsight.risk.Model.Entity.CandleStick;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;
import org.bainsight.OrderType;
import org.bainsight.Proceedable;
import org.bainsight.RiskRequest;
import org.bainsight.TransactionType;
import org.exchange.library.Utils.STRINGS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;
import java.util.concurrent.ExecutionException;


@SpringBootTest
public class RiskTests {


    /**
     * START PORTFOLIO APPLICATION AND SERVICE REGISTRY BEFORE RUNNING TESTS!
     * */



    @Autowired
    private CandleStickRepo stickRepo;

    @Autowired
    private DailyOrderMetaRepo dailyOrderMetaRepo;

    @Autowired
    private RiskManagementService riskManagementService;

    private static int maxAllowedQuantity = 100;
    private static int allowedSpend = 300000;
    private static int allowedOpenOrders = 5;
    private final String ucc = STRINGS.UCC;




    @DynamicPropertySource
    static void updateRedis(DynamicPropertyRegistry registry) {

        registry.add("allowed.open.orders", () -> allowedOpenOrders);
        registry.add("allowed.spend.amount", () -> allowedSpend);
        registry.add("allowed.quantity.per.bid", () -> maxAllowedQuantity);
    }

    @Test
    public void passBid() throws ExecutionException, InterruptedException {

        StreamRecorder<Proceedable> responseObserver = StreamRecorder.create();

        final String SYMBOL = "AAPL";

        Optional<CandleStick> optional = this.stickRepo.findById(SYMBOL);
        CandleStick AAPL = optional.get();


        RiskRequest riskRequest = RiskRequest.newBuilder()
                .setUcc(ucc)
                .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                .setTransactionType(TransactionType.BID)
                .setSymbol(SYMBOL)
                .setQuantity(100)
                .setPrice(AAPL.getHigh() * 1.15)
                .build();

        this.riskManagementService.checkIfProceedable(riskRequest, responseObserver);

        Proceedable proceedable = responseObserver.firstValue().get();


        Assertions.assertNull(responseObserver.getError());
        Assertions.assertTrue(proceedable.getProceedable());

    }


    @Test
    public void passAsk() {
        StreamRecorder<Proceedable> responseObserver = StreamRecorder.create();

        final String SYMBOL = "AAPL";

        Optional<CandleStick> optional = this.stickRepo.findById(SYMBOL);
        CandleStick AAPL = optional.get();


        RiskRequest riskRequest = RiskRequest.newBuilder()
                .setUcc(ucc)
                .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                .setTransactionType(TransactionType.ASK)
                .setSymbol(SYMBOL)
                .setQuantity(1)
                .setPrice(AAPL.getHigh() * 1.15)
                .build();

        this.riskManagementService.checkIfProceedable(riskRequest, responseObserver);

        Proceedable first = responseObserver.getValues().getFirst();
        Assertions.assertTrue(first.getProceedable());
    }



    @Test
    public void failRiskForAboveAllowedQuantity() {
        StreamRecorder<Proceedable> responseObserver = StreamRecorder.create();

        final String SYMBOL = "AAPL";

        Optional<CandleStick> optional = this.stickRepo.findById(SYMBOL);
        CandleStick AAPL = optional.get();


        RiskRequest riskRequest = RiskRequest.newBuilder()
                .setUcc(ucc)
                .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                .setTransactionType(TransactionType.BID)
                .setSymbol(SYMBOL)
                .setQuantity(101)
                .setPrice(AAPL.getHigh() * 1.15)
                .build();

        this.riskManagementService.checkIfProceedable(riskRequest, responseObserver);
        StatusRuntimeException exception = (StatusRuntimeException) responseObserver.getError();

        assert exception != null;
        Status status = exception.getStatus();

        Assertions.assertEquals(Status.OUT_OF_RANGE.withDescription("The maximum allowed order quantity per purchase is ".concat(String.valueOf(maxAllowedQuantity))).toString(), status.toString());

    }

    @Test
    public void priceBoundaryExceptionTest() {
        StreamRecorder<Proceedable> responseObserver = StreamRecorder.create();

        final String SYMBOL = "AAPL";

        Optional<CandleStick> optional = this.stickRepo.findById(SYMBOL);
        CandleStick AAPL = optional.get();


        RiskRequest riskRequest1 = RiskRequest.newBuilder()
                .setUcc(ucc)
                .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                .setTransactionType(TransactionType.BID)
                .setSymbol(SYMBOL)
                .setQuantity(100)
                .setPrice(AAPL.getHigh() * .7)
                .build();

        RiskRequest riskRequest2 = RiskRequest.newBuilder()
                .setUcc(ucc)
                .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                .setTransactionType(TransactionType.BID)
                .setSymbol(SYMBOL)
                .setQuantity(100)
                .setPrice(AAPL.getHigh() * 1.3)
                .build();


        CandleStick candleStick = this.stickRepo.findById(SYMBOL).get();

        double lowest = Math.round(candleStick.getLow() * 0.8);
        this.riskManagementService.checkIfProceedable(riskRequest1, responseObserver);
        Assertions.assertEquals(responseObserver.getError().getMessage(), "OUT_OF_RANGE: The Order cannot be accepted as the lowest acceptable price for the symbol ".concat(SYMBOL).concat(" is ").concat(String.valueOf(lowest)));

        this.riskManagementService.checkIfProceedable(riskRequest2, responseObserver);
        Assertions.assertTrue(responseObserver.getError().getMessage().startsWith("OUT_OF_RANGE: The Order cannot be accepted as the highest acceptable price for the symbol "));

    }


    @Test
    public void pastAllowedOpenOrderLimit(){

        StreamRecorder<Proceedable> responseObserver = StreamRecorder.create();

        final String SYMBOL = "AAPL";

        Optional<CandleStick> optional = this.stickRepo.findById(SYMBOL);
        CandleStick AAPL = optional.get();


        RiskRequest riskRequest = RiskRequest.newBuilder()
                .setUcc(ucc)
                .setOrderType(OrderType.ORDER_TYPE_LIMIT)
                .setTransactionType(TransactionType.BID)
                .setSymbol(SYMBOL)
                .setQuantity(10)
                .setPrice(AAPL.getHigh() * 1.15)
                .build();



        for(int i=0 ; i<6 ; i++){
            this.riskManagementService.checkIfProceedable(riskRequest, responseObserver);
        }

        Assertions.assertEquals(responseObserver.getError().getMessage(), "OUT_OF_RANGE: Order cannot be proceeded as user has too many open orders pending");

    }

}
