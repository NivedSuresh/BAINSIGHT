package org.exchange.library.Utils;

import java.time.LocalDateTime;

public class BainsightUtils {


    public static void sleep(int millis){
        try {Thread.sleep(millis);}
        catch (InterruptedException ignored) {}
    }

    public static boolean isMarketClosed(LocalDateTime now){
        if(now == null) now = LocalDateTime.now();
        return now.getHour() < 9 || now.getHour() > 15 || (now.getHour() == 15 && now.getMinute() > 30);
    }

}
