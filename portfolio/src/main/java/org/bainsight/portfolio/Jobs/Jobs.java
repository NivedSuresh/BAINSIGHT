package org.bainsight.portfolio.Jobs;


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
    public void resetPortfolio(){
        this.jobScheduler.scheduleRecurrently(Cron.daily(7, 0), ResetJob::resetPortfolio);
    }

    @PostConstruct
    public void resetWallet(){
        this.jobScheduler.scheduleRecurrently(Cron.daily(7, 0), ResetJob::resetWallet);
    }


}
