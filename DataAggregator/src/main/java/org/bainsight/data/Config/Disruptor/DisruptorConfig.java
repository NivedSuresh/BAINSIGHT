package org.bainsight.data.Config.Disruptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.aeron.Aeron;
import io.aeron.Publication;
import lombok.RequiredArgsConstructor;
import org.bainsight.data.Config.Disruptor.ThreadFactory.TickEventFactory;
import org.bainsight.data.Config.Election.LeaderConfig;
import org.bainsight.data.Handler.Event.MarketDataHandler;
import org.bainsight.data.Handler.Event.MarketAnalyzer;
import org.bainsight.data.Handler.Event.TickReceivedEventHandler;
import org.bainsight.data.Handler.Exception.TickExceptionHandler;
import org.bainsight.data.Model.Events.TickAcceptedEvent;
import org.bainsight.data.Model.Events.TickReceivedEvent;
import org.bainsight.data.Persistance.CandleStickBuffer;
import org.bainsight.data.Persistance.RecentlyReceivedBuffer;
import org.bainsight.data.Repository.CandleStickRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;

@Configuration
@RequiredArgsConstructor
public class DisruptorConfig {

    @Value("${exchange.id}")
    private String[] exchanges;

    private final ExecutorService snapshotExecutor;

    private MarketDataHandler marketDataHandler;

    private TickReceivedEventHandler receivedEventHandler;

    private final static String USER_SERVICE_CHANNEL;
    private final static Integer USER_SERVICE_STREAM_ID;
    private final LocalTime limitTime = LocalTime.of(15, 30); // 3:30 PM

    static {
        USER_SERVICE_CHANNEL = System.getProperty("aeron.user.service.multicast.channel", "aeron:udp?endpoint=224.0.1.1:40456|interface=localhost|reliable=true");
        USER_SERVICE_STREAM_ID = Integer.getInteger("aeron.user.service.multicast.steam.id",1001);
    }



    @Bean
    public Disruptor<TickReceivedEvent> tickReceivedEventDisruptor(final RecentlyReceivedBuffer recentlyReceivedBuffer,
                                                                   final RingBuffer<TickAcceptedEvent> acceptedBuffer,
                                                                   final ExecutorService recoveryExecutor) {
        Disruptor<TickReceivedEvent> disruptor = new Disruptor<>(
                TickReceivedEvent.TICK_RECEIVED_EVENT_FACTORY,
                2048,
                TickEventFactory.INSTANCE,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
                );

        this.receivedEventHandler = new TickReceivedEventHandler(recentlyReceivedBuffer, acceptedBuffer, exchanges, profiles);
        disruptor.handleEventsWith(receivedEventHandler);

        disruptor.setDefaultExceptionHandler(new TickExceptionHandler<>());

        disruptor.start();


        return disruptor;
    }

    @Bean
    public Disruptor<TickAcceptedEvent> tickAcceptedEventDisruptor(final CandleStickBuffer candleStickBuffer,
                                                                   final Aeron aeron,
                                                                   final ObjectMapper mapper,
                                                                   final ExecutorService greenExecutor,
                                                                   final KafkaTemplate<String, Object> template,
                                                                   final CandleStickRepo candleStickRepo,
                                                                   final RedisTemplate<String, Object> redisTemplate){
        Disruptor<TickAcceptedEvent> disruptor = new Disruptor<>(
                TickAcceptedEvent.TICK_ACCEPTED_EVENT_FACTORY,
                1024,
                TickEventFactory.INSTANCE,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );

        disruptor.setDefaultExceptionHandler(new TickExceptionHandler<>());

        Publication userServicePublication = aeron.addPublication(USER_SERVICE_CHANNEL, USER_SERVICE_STREAM_ID);

        this.marketDataHandler = new MarketDataHandler(
                (byte) 0, (byte) 1,
                candleStickBuffer,
                profiles,
                userServicePublication,
                mapper,
                greenExecutor,
                template,
                candleStickRepo,
                redisTemplate
        );

        disruptor.handleEventsWith(marketDataHandler, new MarketAnalyzer((byte) 0, (byte) 1));

        disruptor.start();

        return disruptor;
    }

    @Bean
    public RingBuffer<TickReceivedEvent> receiverBuffer(final Disruptor<TickReceivedEvent> disruptor){
        return disruptor.getRingBuffer();
    }

    @Bean
    public RingBuffer<TickAcceptedEvent> acceptedBuffer(final Disruptor<TickAcceptedEvent> disruptor){
        return disruptor.getRingBuffer();
    }



    //TODO : FIX SCHEDULED
//    @Scheduled(cron = "0 * 9-16 * * *")
    @Scheduled(fixedRate = 5000)
    public void takeSnapshot(){
//        if (LocalTime.now().isAfter(limitTime)) return;
//        if(!LeaderConfig.IS_LEADER.get()) return;
        System.out.println("Snapshot");
        this.snapshotExecutor.execute(() -> this.marketDataHandler.takeSnapshot());
    }




    @Scheduled(cron = "0 * 9-16 * * *", zone = "Asia/Kolkata")
    public void reset(){
        if (LocalTime.now().isAfter(limitTime)) return;
        try{ this.receivedEventHandler.reset(); }
        catch (Exception e){ System.out.println(e.getMessage()); }
    }

    /* TODO: UNCOMMENT */
//    @Scheduled(fixedRate = 1000)
//    public void checkIfRequireRecovery(){
//        this.receivedEventHandler.requireRecovery();
//    }


    @Value("${spring.profiles.active}")
    private String[] profiles;
    public TickReceivedEventHandler getReceivedEventHandler() {
        for(String profile : profiles){
            if(profile.equals("test")) return this.receivedEventHandler;
        }
        return null;
    }

    public MarketDataHandler getMarketDataHandler() {
        for(String profile : profiles){
            if(profile.equals("test")) return this.marketDataHandler;
        }
        return null;
    }
}