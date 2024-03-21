package org.bainsight.liquidity.Handler.Event;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import gnu.trove.set.hash.TLongHashSet;
import org.bainsight.liquidity.Handler.Persistance.RecentlyReceivedBuffer;
import org.bainsight.liquidity.Model.Events.TickAcceptedEvent;
import org.bainsight.liquidity.Model.Events.TickReceivedEvent;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

//@Component
public class TickReceivedEventHandler implements EventHandler<TickReceivedEvent> {


    private final RecentlyReceivedBuffer bufferManager;

    private final Map<String, Long> previousSequenceMap;
    private final  Map<String, TLongHashSet> missedMap;
    private final RingBuffer<TickAcceptedEvent> acceptedBuffer;
    private boolean isFirstMessage = true;
    private final AtomicBoolean lock;
    private final ExecutorService recoveryExecutor;

    private String[] profiles;

    @Value("${exchange.id}")
    private String[] exchanges;

    public TickReceivedEventHandler(final RecentlyReceivedBuffer bufferManager,
                                    final RingBuffer<TickAcceptedEvent> acceptedBuffer,
                                    final ExecutorService recoveryExecutor,
                                    final @Value("${exchange.id}") String[] exchanges,
                                    final @Value("${spring.profiles.active}") String[] profiles) {
        this.bufferManager = bufferManager;
        this.acceptedBuffer = acceptedBuffer;
        this.recoveryExecutor = recoveryExecutor;
        this.lock = new AtomicBoolean(false);

        this.exchanges = exchanges;
        this.profiles = profiles;

        this.missedMap = new HashMap<>(exchanges.length);
        this.previousSequenceMap = new HashMap<>(exchanges.length);
        for(String exchange : exchanges){
            previousSequenceMap.put(exchange, 0L);
            missedMap.put(exchange, new TLongHashSet());
        }

    }




    @Override
    public void onEvent(TickReceivedEvent recEv, long rSeq, boolean endOfBatch) {

        Tick tick = recEv.getTick();
        String key = tick.getKey();

        Long previous = this.previousSequenceMap.get(tick.getExchange());

        /*
         * TODO: IMPLEMENT JOURNALING
         * SEQUENCE IS SET TO ZERO DURING INITIALIZATION OF THE MAP,
         * THUS CAN NEVER BE ZERO UNLESS CORRUPTED TICK
         * */
        if(previous == null) {
            logInvalidExchangeReceived(tick);
            return;
        }

        long received = tick.getSequenceNumber();
        if(isFirstMessage)
        {
            previous = received - 1;
            this.isFirstMessage = false;
        }

        long expected = previous + 1;

        /* FETCH THE HASHSET WHICH CONSISTS OF ALL THE MISSING SEQUENCE NUMBER FOR THE EXCHANGE */
        TLongHashSet missed = missedMap.get(tick.getExchange());

        missed.remove(tick.getSequenceNumber());

        /*
        * WILL ONLY BE ADDED IF THE TICK IS NOT OUTDATED FOR THE "EXCHANGE:SYMBOL" PAIR
        * IF ADDED, THEN THE UPDATE IS THE LATEST FOR THE SYMBOL THUS CAN BE FORWARDED.
        * */
        boolean put = bufferManager.put(key, tick.getSequenceNumber());

        /* IF ACCEPTED PUBLISH THE EVENT TO THE  ACCEPTED BUFFER */
        if(put) this.acceptedBuffer.publishEvent((accEv, aSeq) -> {
            accEv.setTick(tick);
            accEv.setKey(key);
        });


        if(expected < received) markOutOfOrder(expected, received, missed);

        /* UPDATE THE LAST RECEIVED */
        updateLastReceived(received, tick.getExchange());


        recEv.clear();
    }

    private void logInvalidExchangeReceived(Tick tick) {
        System.out.println("INVALID EXCHANGE RECEIVED FOR THE SYMBOL WITH SEQUENCE " + tick.getSequenceNumber() + "AND THE SYMBOL ".concat(tick.getSymbol()).concat(" WITH THE TIME STAMP ").concat(tick.getTickTimestamp().toString()).concat(". BROKER RECORDED TIME: ").concat(Instant.now().toString()));
    }

    private void updateLastReceived(long received, String exchange) {
        this.previousSequenceMap.put(exchange, Math.max(received, previousSequenceMap.get(exchange)));
    }

    private void markOutOfOrder(long expected, long received, TLongHashSet missed) {
        for(String exchange : exchanges){
            System.out.println("MISSING MESSAGE COUNT FOR " + exchange + ": " + missed.size());
        }
        while (expected < received) missed.add(expected++);
    }


    public void requireRecovery(){
//        System.out.println("recovery");
//        Map<String, TLongHashSet> recoveryMap = new HashMap<>(this.missedMap);
//        this.recoveryExecutor.execute(() -> requestTcpRecovery(recoveryMap));
    }


    /* TODO: TCP REQUEST IMPLEMENTATION FOR TICK RECOVERY IF NOT RECEIVED */
    /**
     * In case of missing ticks, the broker can make a tcp request to the exchange.
     * The broker is only allowed to make a certain number of requests (decided by the exchange)
     * every minute.
     * */
    private void requestTcpRecovery(Map<String, TLongHashSet> recovery)
    {
        System.out.println(recovery);
    }


    /*
    * RESET SEQUENCE MAP, MISSED MAP
    * */
    public void reset() {
        for(String exchange : exchanges){
            this.missedMap.get(exchange).clear();
            this.previousSequenceMap.put(exchange, 0L);
        }
    }

    public RecentlyReceivedBuffer getBufferManager() {
        for(String profile : profiles){
            if(profile.equals("test")) return this.bufferManager;
        }
        return null;
    }
}
