package org.bainsight.liquidity.Jobs;

import lombok.RequiredArgsConstructor;
import org.bainsight.liquidity.Handler.Persistance.CandleStickBuffer;
import org.bainsight.liquidity.Handler.Persistance.RecentlyReceivedBuffer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResetJobs {

    private final RecentlyReceivedBuffer recentlyReceivedBuffer;
    private final CandleStickBuffer candleStickBuffer;

    @Scheduled(cron = "0 0 16 * * *")
    public void reset(){
        this.recentlyReceivedBuffer.reset();
        this.candleStickBuffer.reset();
    }

}
