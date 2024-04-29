package org.bainsight.order.Jobs;


import lombok.RequiredArgsConstructor;
import org.bainsight.UpdateStatusRequest;
import org.bainsight.order.GrpcService.OrderPersistance.GrpcOrderService;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloseOpenJob {


    private final GrpcOrderService grpcOrderService;

    @Job(name = "partially-fill-status-update", retries = 5)
    public void partiallyFillAllOpen(){
        this.grpcOrderService.partiallyFillAllOpen();
    }

}
