package org.bainsight.liquidity.Listener;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MessageRangeBuffer {

    private Map<String, Long> rangeMap;
    private AtomicLong lock;

    public MessageRangeBuffer() {
        this.rangeMap = new HashMap<>();
        this.lock = new AtomicLong(0);
    }


    public void reset(){
        this.rangeMap = new HashMap<>();
        this.lock = new AtomicLong();
    }


    public boolean put(String key, Long value){
        Long current = rangeMap.get(key);
        if(current == null || current < value) {
            rangeMap.put(key, value);
            lock.incrementAndGet();
            return true;
        }
        return false;
    }

}
