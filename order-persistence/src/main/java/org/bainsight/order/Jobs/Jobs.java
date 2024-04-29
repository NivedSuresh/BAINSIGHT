package org.bainsight.order.Jobs;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Jobs {


    private final JobScheduler jobScheduler;

    @PostConstruct
    public void closeOpenOrders(){
        this.jobScheduler.scheduleRecurrently(Cron.daily(21, 4), CloseOpenJob::partiallyFillAllOpen);
    }


}
