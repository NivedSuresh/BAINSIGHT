package org.bainsight.liquidity.Handler.Persistance;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MessageBufferManager {

    private Map<String, Long> managerMap;


    public MessageBufferManager() {
        this.managerMap = new HashMap<>();
    }


    /**
     * WILL ONLY BE ADDED IF THE TICK IS NOT OUTDATED FOR THE "EXCHANGE:SYMBOL" PAIR
     * ie if the now received {NSE:AAPL = 2896 and the persisted {NSE:AAPL = 2996}
     * then no update will be made.
     * */
    public boolean put(String key, Long value){
        Long current = managerMap.get(key);
        if(current == null || current < value) {
            managerMap.put(key, value);
            return true;
        }
        return false;
    }



    public void reset() {
        this.managerMap.clear();
    }

}
