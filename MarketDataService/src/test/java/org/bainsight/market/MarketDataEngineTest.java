package org.bainsight.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import org.bainsight.market.Config.Disruptor.DisruptorConfig;
import org.bainsight.market.Handler.Event.CandleHandler;
import org.bainsight.market.Handler.Persistance.CandleStickBuffer;
import org.bainsight.market.Handler.Persistance.RecentlyReceivedBuffer;
import org.bainsight.market.Listener.MessageReceiveBuffer;
import org.bainsight.market.Model.Entity.CandleStick;
import org.bainsight.market.Model.Dto.VolumeWrapper;
import org.bainsight.market.Models.TestStick;
import org.bainsight.market.Models.PushTo;
import org.bainsight.market.Models.TickerEx;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;


@SpringBootTest
public class MarketDataEngineTest {

    @Autowired
    private DisruptorConfig disruptorConfig;

    private final MarketDataGenerator marketData = new MarketDataGenerator();

    private Map<String, TestStick> testStickMap = new HashMap<>();

    private final Random random= new Random();

    @Autowired
    private ObjectMapper mapper;
    private final Queue<Tick> backupBuffer = new ArrayBlockingQueue<>(100);

    @Autowired
    private MessageReceiveBuffer messageReceiveBuffer;

    private RecentlyReceivedBuffer recent;

    private final Map<String, List<VolumeWrapper>> volumeMap = new HashMap<>();

    private final ZoneId zoneId = ZoneId.of("Asia/Kolkata");


    @MockBean
    private Aeron aeron;

    @MockBean
    MediaDriver driver;


    /**
     * Evoking this test multiple time because each time when the test is evoked,
     * the persisted snapshot also gets cleared, similarly in production, a snapshot
     * is taken every 1 minute thus clearing the current Market state.
     * <p>
     * This test here would send 1000 * 100 * 200 messages in total,
     * will take 100 snapshots (no sleep) and compare
     * */
    @Test
    void testAFewTimes() throws JsonProcessingException {
        for(int i=0 ; i<1000 ; i++){
            evokeTest();
        }
    }



    public void evokeTest() throws JsonProcessingException {



        this.recent = disruptorConfig.getReceivedEventHandler().getBufferManager();

        //FOR EACH ITERATION, 200 MESSAGES ARE SENT 100 FROM PRIMARY AND 100 FROM SECONDARY
        for(int i=0 ; i<100 ; i++){
            //Send updates
            this.sendUpdates(true);

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




    /**
     * Will validate the snapshot.
     * */
    public void assertSnapshot(){
        //ASSERTION 2
        CandleHandler candleHandler = disruptorConfig.getCandleHandler();
        CandleStickBuffer candleStickBuffer = candleHandler.getCandleStickBuffer();
        Map<String, TestStick> testStickMap = this.testStickMap;
        Map<String, CandleStick> snapshot = candleStickBuffer.getSnapshot(true);


        for(String key : testStickMap.keySet()){
            TestStick testStick = testStickMap.get(key);
            CandleStick candleStick = snapshot.get(key);

            Assertions.assertEquals(testStick.getChange(), candleStick.getChange());
            Assertions.assertEquals(testStick.getLow(), candleStick.getLow());
            Assertions.assertEquals(testStick.getHigh(), candleStick.getHigh());
            Assertions.assertEquals(testStick.getOpen(), candleStick.getOpen());
            Assertions.assertEquals(testStick.getClose(), candleStick.getClose());
            Assertions.assertEquals(testStick.getVolume(), candleStick.getVolume());

        }

        /* reset open close map as snapshots also get reset */
        this.testStickMap.clear();
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

        generateTestStick(tick);

        orderBook.set(count, tick);
        if(++count % random == 0) return count;
        multicastTick(tick);
        return count;
    }

    private void generateTestStick(Tick tick) {
        TestStick testStick = this.testStickMap.get(tick.getSymbol());
        final double lastTradedPrice = tick.getLastTradedPrice();
        long volume = updateVolumeMap(tick);
        if(testStick == null)
        {
            testStick = TestStick.builder()
                    .timeStamp(ZonedDateTime.ofInstant(tick.getTickTimestamp(), zoneId))
                    .low(lastTradedPrice)
                    .high(lastTradedPrice)
                    .open(lastTradedPrice)
                    .close(lastTradedPrice)
                    .volume(volume)
                    .change(0.0)
                    .build();
            this.testStickMap.put(tick.getSymbol(), testStick);
        }
        else
        {
            if(testStick.getTimeStamp().isAfter(ZonedDateTime.ofInstant(tick.getTickTimestamp(), zoneId))) return;;
            testStick.setTimeStamp(ZonedDateTime.ofInstant(tick.getTickTimestamp(), zoneId));
            testStick.setLow(Math.min(testStick.getLow(), tick.getLastTradedPrice()));
            testStick.setHigh(Math.max(testStick.getHigh(), tick.getLastTradedPrice()));
            testStick.setClose(tick.getLastTradedPrice());
            testStick.setChange(Math.round(lastTradedPrice - testStick.getOpen()));
            testStick.setVolume(volume);
            this.testStickMap.put(tick.getSymbol(), testStick);
        }
    }

    private long updateVolumeMap(Tick tick)
    {
        String exchange = tick.getExchange();
        String symbol = tick.getSymbol();
        List<VolumeWrapper> volumeWrappers = this.volumeMap.get(symbol);
        long totalVolume = 0;
        if(volumeWrappers == null)
        {
            volumeWrappers = new ArrayList<>();
            volumeWrappers.add(new VolumeWrapper(exchange, tick.getVolume()));
            this.volumeMap.put(symbol, volumeWrappers);
            totalVolume = tick.getVolume();
        }
        else
        {
            boolean found = false;
            for (VolumeWrapper volumeWrapper : volumeWrappers) {
                if (volumeWrapper.getExchange().equals(exchange)) {
                    found = true;
                    volumeWrapper.setVolume(tick.getVolume());
                }
                totalVolume += volumeWrapper.getVolume();
            }
            if (!found) {
                volumeWrappers.add(new VolumeWrapper(exchange, tick.getVolume()));
                totalVolume += tick.getVolume();
            }
        }
        return totalVolume;
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
