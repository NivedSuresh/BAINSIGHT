package org.bainsight.liquidity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bainsight.liquidity.Config.Disruptor.DisruptorConfig;
import org.bainsight.liquidity.Handler.Event.CandleHandler;
import org.bainsight.liquidity.Handler.Persistance.CandleStickBuffer;
import org.bainsight.liquidity.Handler.Persistance.RecentlyReceivedBuffer;
import org.bainsight.liquidity.Listener.MessageReceiveBuffer;
import org.bainsight.liquidity.Model.Dto.CandleStick;
import org.bainsight.liquidity.Models.PushTo;
import org.bainsight.liquidity.Models.TickerEx;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
public class MarketDataEngineTest {



    @Autowired
    private DisruptorConfig disruptorConfig;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final MarketDataGenerator marketData = new MarketDataGenerator();

    private final Random random= new Random();

    @Autowired
    private ObjectMapper mapper;
    private final Queue<Tick> backupBuffer = new ArrayBlockingQueue<>(100);

    @Autowired
    private MessageReceiveBuffer messageReceiveBuffer;

    private RecentlyReceivedBuffer recent;

    private final ZoneId zoneId = ZoneId.of("Asia/Kolkata");


    /**
     * Evoking this test multiple time because each time when the test is evoked,
     * the persisted snapshot of also gets cleared, similarly in production, a snapshot
     * is taken every 1 minute thus clearing the current Market state.
     * <p>
     * This test here would send 100 * 1000 * 200 messages in total,
     * will take 100 snapshots (no sleep) and compare
     * */
    @Test
    void testAFewTimes() throws JsonProcessingException {
        for(int i=0 ; i<10 ; i++){
            evokeTest();
        }
    }



    public void evokeTest() throws JsonProcessingException {

        this.recent = disruptorConfig.getReceivedEventHandler().getBufferManager();

        //FOR EACH ITERATION, 200 MESSAGES ARE SENT 100 FROM PRIMARY AND 100 FROM SECONDARY
        for(int i=0 ; i<100 ; i++){
            //Send updates
            this.sendUpdates(true);

            // A small delay between primary and secondary/backup update
//            Thread.sleep(50);
        /*
            Some updates are skipped when primary is set to true during testing, this
            is done to replicate the behavior of UDP multicasting. (NB: UDP is only
            inconsistent if the writer is faster than the reader. if that's the case,
            then packets are dropped)

            If primary is set to false, all updates sent during the current session
            including the skipped ones will be resented.

            All updates are again resend to ensure reliability and brokers can subscribe
            to two channels the primary and secondary.
        */
            //Resend all updates, including the ones that were skipped during the first cast.
            this.sendUpdates(false);


        }


        List<Tick> bseOrderBook = this.marketData.getExchangeSpecificOrderBook().get("BSE");
        List<Tick> nseOrderBook = this.marketData.getExchangeSpecificOrderBook().get("NSE");


        bseOrderBook.stream().filter(Objects::nonNull).forEach(this::assertSequence);
        nseOrderBook.stream().filter(Objects::nonNull).forEach(this::assertSequence);

        assertSnapshot();


    }


    /**
     * Will validate the snapshot.
     * */
    public void assertSnapshot(){
        //ASSERTION 2
        CandleHandler candleHandler = disruptorConfig.getCandleHandler();
        CandleStickBuffer candleStickBuffer = candleHandler.getCandleStickBuffer();
        Map<String, CandleStick> snapshot = candleStickBuffer.getSnapshot();

        Map<String, List<Tick>> exchangeSpecificOrderBook = this.marketData.getExchangeSpecificOrderBook();
        List<Tick> nse = exchangeSpecificOrderBook.get("NSE");
        List<Tick> bse = exchangeSpecificOrderBook.get("BSE");


        List<CandleStick> sentSticksCombined = getSentSticksCombined(nse, bse);

        for(CandleStick stick : sentSticksCombined){

            double high = snapshot.get(stick.getSymbol()).getHigh();
            double low = snapshot.get(stick.getSymbol()).getLow();
            double change = snapshot.get(stick.getSymbol()).getChange();
            double open = snapshot.get(stick.getSymbol()).getOpen();
            double close = snapshot.get(stick.getSymbol()).getClose();
            long volume = snapshot.get(stick.getSymbol()).getVolume();

            Assertions.assertEquals(close, stick.getClose());
            Assertions.assertEquals(high, stick.getHigh());
            Assertions.assertEquals(change, stick.getChange());
            Assertions.assertEquals(open, stick.getOpen());
            Assertions.assertEquals(low, stick.getLow());
            Assertions.assertEquals(volume, stick.getVolume());

        }
    }


    /**
     * Will verify the code to persist only the latest update is working.
     * If orders arrive out of order, then older updates shouldn't overwrite newer updates
     * */
    private void assertSequence(Tick tick) {


        // ASSERTION 1
       Map<String, Long> managerMap = recent.getManagerMap();
       String key = tick.getKey();


       Long seq = managerMap.get(tick.getKey());

            /*
                The update in the OrderBook { exchange:symbol = sequence }
                is already sent and processed, so the put requested should return false.
                Update is only added if
                new { exchange:symbol = sequence } > current { exchange:symbol = sequence }
            */
        Assertions.assertFalse(recent.put(key, tick.getSequenceNumber()));

            /*
                Checking if the latest data is matching, i.e., does
                OrderBook { exchange:symbol = sequence }
                               equals to
                RecentlyReceived { exchange:symbol = sequence }
            */
        Assertions.assertEquals(tick.getSequenceNumber(), seq);


    }

    private List<CandleStick> getSentSticksCombined(List<Tick> nse, List<Tick> bse) {


        List<CandleStick> sticks = new ArrayList<>();

        for(Tick nseTick : nse){
            for(Tick bseTick : bse){
                if(nseTick == null && bseTick == null) break;
                if(nseTick == null) nseTick = new Tick();
                if(bseTick == null) bseTick = new Tick();

                if(Objects.equals(nseTick.getSymbol(), bseTick.getSymbol())){
                    sticks.add(combineTicks(nseTick, bseTick));
                }
            }
        }


        return sticks;

    }

    private CandleStick combineTicks(Tick nseTick, Tick bseTick) {


        double low = Math.min(bseTick.getLowPrice(), nseTick.getLowPrice());
        double high = Math.max(bseTick.getHighPrice(), nseTick.getHighPrice());
        double open = Math.min(bseTick.getOpenPrice(), nseTick.getOpenPrice());
        double close = Math.min(bseTick.getClosePrice(), nseTick.getClosePrice());
        double change = Math.min(bseTick.getChange(), nseTick.getChange());
        long volume = bseTick.getVolume() + nseTick.getVolume();



        Instant largest = bseTick.getTickTimestamp() == null ? nseTick.getTickTimestamp() :
                          (bseTick.getTickTimestamp().isBefore(nseTick.getTickTimestamp()) ?
                          nseTick.getTickTimestamp() : bseTick.getTickTimestamp());


        return CandleStick.builder()
                .symbol(nseTick.getSymbol())
                .change(change)
                .volume(volume)
                .close(close)
                .low(low)
                .high(high)
                .open(open)
                .timeStamp(ZonedDateTime.ofInstant(largest, zoneId))
                .build();


    }

    public void sendUpdates(boolean primary) throws JsonProcessingException {
        if(primary && !marketData.getDequeA().isEmpty()) bufferA();
        else if(primary && !marketData.getDequeB().isEmpty()) bufferB();
        else if(!primary) bufferBackup();
    }

    private void bufferBackup() throws JsonProcessingException {
        while (!backupBuffer.isEmpty()){
            Tick tick = backupBuffer.poll();
            if(tick == null) return;
            multicastTick(tick);
        }
    }

    private void bufferA() throws JsonProcessingException {

        int count = 0;
        int random = (int) (Math.random() * 100) + 1;
        while (!marketData.getDequeA().isEmpty()){
            TickerEx meta = marketData.getDequeA().poll();
            Tick tick = marketData.getRandomTick(meta, PushTo.QUEUE_B, getExchange());
            count = backupAndMulticast(count, random, tick);
        }
    }


    public void bufferB() throws JsonProcessingException {


        int count = 0;
        int random = new Random().nextInt(50, 100);
        while (!marketData.getDequeB().isEmpty()){
            TickerEx meta = marketData.getDequeB().poll();
            Tick tick = marketData.getRandomTick(meta, PushTo.QUEUE_A, getExchange());

            count = backupAndMulticast(count, random, tick);
        }
    }

    private int backupAndMulticast(int count, int random, Tick tick) throws JsonProcessingException {
        backupBuffer.offer(tick);
        List<Tick> orderBook = this.marketData.getExchangeSpecificOrderBook().get(tick.getExchange());


        orderBook.set(count, tick);
        if(++count % random == 0) return count;
        multicastTick(tick);
        return count;
    }

    private void multicastTick(Tick tick) throws JsonProcessingException {
        String jsonfiedTick = mapper.writeValueAsString(tick);
        byte[] data = jsonfiedTick.getBytes();
        this.messageReceiveBuffer.offer(data);
    }



    private String getExchange(){
        int random = this.random.nextInt(1, 3);
        return random % 2 == 0 ? "NSE" : "BSE";
    }

}
