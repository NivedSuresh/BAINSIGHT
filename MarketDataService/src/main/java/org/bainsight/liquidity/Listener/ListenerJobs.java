package org.bainsight.liquidity.Listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
class ListenerJobs {

    private final MessageRangeBuffer messageRangeBuffer;


    /* TODO : IMPLEMENT KILL SWITCH FOR UDP LISTENERS IF NECESSARY */

    @Scheduled(cron = "0 0 16 * * *")
    public void reset(){
        messageRangeBuffer.reset();
    }


}
