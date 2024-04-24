package org.bainsight.history.Jobs;


import lombok.RequiredArgsConstructor;
import org.bainsight.history.Data.HistoryServiceImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@EnableScheduling
@RequiredArgsConstructor
class GainerLoserJobs {

    private final HistoryServiceImpl historyService;


    @Scheduled(cron = "0 * 9-16 * * *", zone = "Asia/Kolkata")
    public void cacheLosersAndGainers()
    {
        LocalTime time = LocalTime.now();

        if(time.getMinute() % 5 != 1 || time.isAfter(LocalTime.of(15, 30))) return;

        this.historyService.cacheLosersGainersForTheDay();
    }

}
