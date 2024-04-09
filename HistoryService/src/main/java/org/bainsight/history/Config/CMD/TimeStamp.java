package org.bainsight.history.Config.CMD;

import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimeStamp implements CommandLineRunner {

    public List<LocalDateTime> _1W;
    public List<LocalDateTime> _1M;
    public List<LocalDateTime> _1Y;
    public List<LocalDateTime> _3Y;

    public TimeStamp() {
        initializeTimestamps();
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void initializeTimestamps() {
        this._1W = initializeMinuteBased(7, 30);
        this._1M = initializeMinuteBased(28, 120);
        this._1Y = initializeDayBased(1, 3);
        this._3Y = initializeDayBased(3, 15);
    }

    private List<LocalDateTime> initializeDayBased(int minusYears, int plusDays) {
        List<LocalDateTime> times = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 15, 30, 0, 0);
        LocalDateTime aMonthBefore = today.minusYears(minusYears);


        while (aMonthBefore.isBefore(today))
        {
            times.add(aMonthBefore);
            aMonthBefore = aMonthBefore.plusDays(plusDays);
        }

        return times;
    }


    private List<LocalDateTime> initializeMinuteBased(long minusDays, long plusMinutes){
        List<LocalDateTime> times = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), 0, 0);
        LocalDateTime past = today.minusDays(minusDays);


        while (past.isBefore(today))
        {
            if(past.getDayOfWeek() == DayOfWeek.SUNDAY)
            {
                past = past.plusDays(1);
            }

            int hour =  past.getHour();
            int minute = past.getMinute();


            if(hour >= 9 && (hour < 15 || (hour == 15 && minute <= 30)))
            {
                times.add(past);
                past = past.plusMinutes(plusMinutes);
                continue;
            }

            LocalDateTime nextDay = past.plusDays(1);
            past = LocalDateTime.of(nextDay.getYear(), nextDay.getMonth(), nextDay.getDayOfMonth(), 9, 0, 0);
        }
        return times;
    }


    @Override
    public void run(String... args){}
}
