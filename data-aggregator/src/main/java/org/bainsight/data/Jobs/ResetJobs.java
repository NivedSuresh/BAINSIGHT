package org.bainsight.data.Jobs;

import lombok.RequiredArgsConstructor;
import org.bainsight.data.Persistence.CandleStickBuffer;
import org.bainsight.data.Persistence.RecentlyReceivedBuffer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResetJobs {

    private final RecentlyReceivedBuffer recentlyReceivedBuffer;
    private final CandleStickBuffer candleStickBuffer;

    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Kolkata")
    public void reset(){
        this.recentlyReceivedBuffer.reset();
        this.candleStickBuffer.reset();
    }

}
