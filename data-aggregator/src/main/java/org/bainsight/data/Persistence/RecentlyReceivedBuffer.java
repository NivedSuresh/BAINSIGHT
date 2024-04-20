package org.bainsight.data.Persistence;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RecentlyReceivedBuffer {

    private final Map<String, Long> managerMap;


    public RecentlyReceivedBuffer() {
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


    @Value("${spring.profiles.active}")
    private String[] profiles;
    public Map<String, Long> getManagerMap() {
        for(String profile : profiles){
            if(profile.equals("test")) return this.managerMap;
        }
        return null;
    }

    public void reset() {
        this.managerMap.clear();
    }

}
