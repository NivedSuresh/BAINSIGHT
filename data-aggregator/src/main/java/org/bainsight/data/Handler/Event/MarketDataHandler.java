package org.bainsight.data.Handler.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.EventHandler;
import io.aeron.Publication;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.agrona.BufferUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.bainsight.data.Config.Election.LeaderConfig;
import org.bainsight.data.Model.Entity.CandleStick;
import org.bainsight.data.Model.Events.TickAcceptedEvent;
import org.bainsight.data.Persistence.CandleStickBuffer;
import org.bainsight.data.Persistence.Repository.CandleStickRepo;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


@AllArgsConstructor
@Slf4j
public class MarketDataHandler implements EventHandler<TickAcceptedEvent> {

    private final byte shard;
    private final byte totalHandlers;
    private final CandleStickBuffer candleStickBuffer;
    private final String[] profiles;
    private final Publication publication;
    private final ObjectMapper mapper;
    private final ExecutorService greenExecutor;
    private final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(300, 64));
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CandleStickRepo candleStickRepo;
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public void onEvent(TickAcceptedEvent event, long sequence, boolean endOfBatch) {

        if (!LeaderConfig.IS_LEADER.get() || sequence % totalHandlers != shard) return;
        Tick tick = event.getTick();
        CandleStick combinedStick = this.candleStickBuffer.updateAndGetCandleStick(tick);


        /* TODO: UNCOMMENT WHEN HOSTING */
        this.greenExecutor.execute(() -> {
            final byte[] data = this.serialize(combinedStick);
            if(data.length == 0) return;
            buffer.putBytes(0, data);

            final long result = publication.offer(buffer);

//            System.out.println("Updating cluster");
            this.updateRedisCluster(combinedStick);

            this.validateResult(result);
        });

    }

    private void updateRedisCluster(CandleStick combinedStick) {
        try{ this.candleStickRepo.save(combinedStick); }
        catch (Exception  e)
        {
            /* TODO: IMPLEMENT LOGGING */
            System.out.println("Exception when persisting to Redis: " + e.getMessage());
            System.out.println("TODO: IMPLEMENT LOGGING");
        }
    }

    private void validateResult(long result) {
        /* TODO: IMPLEMENT JOURNALING */
        if (result > 0)
        {
            System.out.println("sent!");
        }
        else if (result == Publication.BACK_PRESSURED)
        {
            System.out.println("Offer failed due to back pressure");
        }
        else if (result == Publication.NOT_CONNECTED)
        {
            System.out.println("Offer failed because publisher is not connected to a subscriber");
        }
        else if (result == Publication.ADMIN_ACTION)
        {
            System.out.println("Offer failed because of an administration action in the system");
        }
        else if (result == Publication.CLOSED)
        {
            System.out.println("Offer failed because publication is closed");
//            break;
        }
        else if (result == Publication.MAX_POSITION_EXCEEDED)
        {
            System.out.println("Offer failed due to publication reaching its max position");
//            break;
        }
        else
        {
            System.out.println("Offer failed due to unknown reason: " + result);
        }

        if (!this.publication.isConnected())
        {
            log.warn("No active subscribers detected");
        }
    }

    private byte[] serialize(CandleStick combinedStick) {
        try {
            return mapper.writeValueAsBytes(combinedStick);
        } catch (JsonProcessingException e) {
            /* TODO: IMPLEMENT JOURNALING */
            log.error("Error while serializing");
            return new byte[0];
        }
    }



    /**
     * TODO: USE SCYLLA DB TO PERSIST THE SNAPSHOT
     * */
    public void takeSnapshot(){
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime modifiedNow = ZonedDateTime.of(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), 0, 0,
                now.getZone());

        Map<String, CandleStick> sticks = this.candleStickBuffer.getSnapshot(true);

        this.greenExecutor.execute(() -> {
            for(String symbol : sticks.keySet()){
                CandleStick candleStick = sticks.get(symbol);
                /*
                 * Update the timestamp for the CandleStick.
                 **/
                candleStick.setTimeStamp(modifiedNow);
                /*
                * There could be 2000+ candle sticks, ie one for each symbol
                * Should create a by excluding ExchangePrices or skip the
                * unnecessary Garbage.
                * */
                candleStick.setExchangePrices(null);
                this.kafkaTemplate.send("candle_sticks", candleStick);
            }
        });

        /* Fetch sticks that are not in JVM from Redis */
        this.greenExecutor.execute(() -> {
            Set<String> activeSymbolsLastMinute = sticks.keySet().stream()
                    .map("CandleStick:"::concat)
                    .collect(Collectors.toSet());
            Set<String> allTradableSymbols = this.redisTemplate.keys("CandleStick:*");

            if(allTradableSymbols == null) return;

            allTradableSymbols.forEach(key -> {
                if(!activeSymbolsLastMinute.contains(key))
                {
                    fetchAbsentAndSnapshot(key, modifiedNow);
                }
            });
        });
    }

    private void fetchAbsentAndSnapshot(String key, ZonedDateTime now) {
        key = key.substring(12);
        this.candleStickRepo.findById(key).ifPresent(candleStick -> modifyAsAbsentThenStream(candleStick, now));
    }



    /**
     * The symbol's for which no trade was executed in the last minute is modified and
     * persisted in the history service.
     **/
    public void modifyAsAbsentThenStream(CandleStick stick, ZonedDateTime now){
        /* TODO: VERIFY IF IT'S WORKING FINE AFTER COMMENTING */
        stick.setTimeStamp(now);
        this.kafkaTemplate.send("candle_sticks", stick);
    }


    public CandleStickBuffer getCandleStickBuffer(){
        for(String profile: profiles){
            if(profile.equals("test")) return this.candleStickBuffer;
        }
        return null;
    }

}
