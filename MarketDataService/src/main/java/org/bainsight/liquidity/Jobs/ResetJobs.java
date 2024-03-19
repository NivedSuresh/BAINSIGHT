package org.bainsight.liquidity.Jobs;

import lombok.RequiredArgsConstructor;
import org.bainsight.liquidity.Handler.Event.TickReceivedEventHandler;
import org.bainsight.liquidity.Handler.Persistance.CandleManagerBuffer;
import org.bainsight.liquidity.Handler.Persistance.MessageBufferManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResetJobs {


    private final MessageBufferManager messageBufferManager;
    private final TickReceivedEventHandler receivedEventHandler;
    private final CandleManagerBuffer candleManagerBuffer;


    @Scheduled(cron = "0 0 16 * * *")
    public void reset(){
        this.receivedEventHandler.reset();
        this.messageBufferManager.reset();
        this.candleManagerBuffer.reset();
    }
}
