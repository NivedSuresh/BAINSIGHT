package org.bainsight.market.Handler.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.EventHandler;
import io.aeron.Publication;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.agrona.BufferUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.bainsight.market.Config.Election.LeaderConfig;
import org.bainsight.market.Handler.Persistance.CandleStickBuffer;
import org.bainsight.market.Model.Entity.CandleStick;
import org.bainsight.market.Model.Events.TickAcceptedEvent;
import org.bainsight.market.Repository.CandleStickRepo;
import org.exchange.library.Dto.MarketRelated.Tick;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;


@AllArgsConstructor
@Slf4j
public class CandleHandler implements EventHandler<TickAcceptedEvent> {

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


        this.greenExecutor.execute(() -> {
            final byte[] data = this.serialize(combinedStick);
            if(data.length == 0) return;
            buffer.putBytes(0, data);

            final long result = publication.offer(buffer);

            this.updateRedisCluster(combinedStick);

            this.validateResult(result);
        });

    }

    private void updateRedisCluster(CandleStick combinedStick) {
        try{
            String jsonifiedStick = this.mapper.writeValueAsString(combinedStick);
            this.redisTemplate.opsForValue().set(combinedStick.getSymbol(), jsonifiedStick);
        }catch (JsonProcessingException  e){
            /* TODO: IMPLEMENT LOGGING */
            System.out.println("TODO: IMPLEMENT LOGGING");
        }
    }

    private void validateResult(long result) {
        /* TODO: IMPLEMENT JOURNALING */
        if (result > 0)
        {
//            System.out.println("sent!");
        }
        else if (result == Publication.BACK_PRESSURED)
        {
//            System.out.println("Offer failed due to back pressure");
        }
        else if (result == Publication.NOT_CONNECTED)
        {
//            System.out.println("Offer failed because publisher is not connected to a subscriber");
        }
        else if (result == Publication.ADMIN_ACTION)
        {
//            System.out.println("Offer failed because of an administration action in the system");
        }
        else if (result == Publication.CLOSED)
        {
//            System.out.println("Offer failed because publication is closed");
//            break;
        }
        else if (result == Publication.MAX_POSITION_EXCEEDED)
        {
//            System.out.println("Offer failed due to publication reaching its max position");
//            break;
        }
        else
        {
//            System.out.println("Offer failed due to unknown reason: " + result);
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
        this.greenExecutor.execute(() -> {
            Map<String, CandleStick> sticks = this.candleStickBuffer.getSnapshot(true);
            for(String symbol : sticks.keySet()){
                this.kafkaTemplate.send("candle_sticks", sticks.get(symbol));
            }

            Set<String> activeSymbolsLastMinute = sticks.keySet();
            Set<String> allTradableSymbols = this.redisTemplate.keys("*");


            if(allTradableSymbols == null) return;

            allTradableSymbols.forEach(key -> {
                if(!activeSymbolsLastMinute.contains(key)){
                    CandleStick stick = (CandleStick) this.redisTemplate.opsForValue().get(key);
                    if(stick != null) modifyAsAbsentThenStream(stick);
                }
            });

        });
    }


    /**
     * The symbol's for which no trade was executed in the last minute is modified and
     * persisted in the history service.
     * */
    public void modifyAsAbsentThenStream(CandleStick stick){
        stick.setLow(0.0);
        stick.setHigh(0.0);
        stick.setOpen(0.0);
        stick.setClose(0.0);
        this.kafkaTemplate.send("candle_sticks", stick);
    }


    public CandleStickBuffer getCandleStickBuffer(){
        for(String profile: profiles){
            if(profile.equals("test")) return this.candleStickBuffer;
        }
        return null;
    }

}
