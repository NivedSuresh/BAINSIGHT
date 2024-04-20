package com.bainsight.risk.Config.Redis;



import com.bainsight.risk.Model.Entity.CandleStick;
import com.bainsight.risk.Model.Entity.DailyOrderMeta;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.time.*;
import java.util.List;

public class CandleStickKeySpaceConfig extends KeyspaceConfiguration {
    @Override
    protected Iterable<KeyspaceSettings> initialConfiguration() {

        KeyspaceSettings candleStick = new KeyspaceSettings(CandleStick.class, "CandleStick");

        KeyspaceSettings dailyOrderMeta = new KeyspaceSettings(DailyOrderMeta.class, "DailyOrderMeta");

        /* TODO: UNCOMMENT BEFORE DEPLOYING */
//        dailyOrderMeta.setTimeToLive(calculateTTLInSecondsUntil3_30PM());

        return List.of(candleStick, dailyOrderMeta );
    }

    public static long calculateTTLInSecondsUntil3_30PM() {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        if(now.getDayOfWeek() == DayOfWeek.SUNDAY) return 0;

        // Get today's date at 5 PM
        LocalTime fivePM = LocalTime.of(0, 5);

        // Combine today's date with 5 PM
        LocalDateTime todayAt5PM = LocalDateTime.of(now.toLocalDate(), fivePM);

        // If it's already past 5 PM, calculate for tomorrow
        if (now.toLocalTime().isAfter(fivePM)) {
            todayAt5PM = todayAt5PM.plusDays(1);
        }

        // Convert LocalDateTime to ZonedDateTime to handle time zones
        ZonedDateTime zonedDateTime = todayAt5PM.atZone(ZoneId.systemDefault());

        // Calculate the duration until 5 PM
        Duration duration = Duration.between(ZonedDateTime.now(), zonedDateTime);

        // Return the duration in seconds
        return duration.getSeconds();
    }

}
