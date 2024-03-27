package org.bainsight.market.Config.Disruptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.aeron.Aeron;
import io.aeron.Publication;
import lombok.RequiredArgsConstructor;
import org.bainsight.market.Config.Disruptor.ThreadFactory.TickEventFactory;
import org.bainsight.market.Config.Election.LeaderConfig;
import org.bainsight.market.Handler.Event.CandleHandler;
import org.bainsight.market.Handler.Event.MarketAnalyzer;
import org.bainsight.market.Handler.Event.TickReceivedEventHandler;
import org.bainsight.market.Handler.Exception.TickExceptionHandler;
import org.bainsight.market.Handler.Persistance.CandleStickBuffer;
import org.bainsight.market.Handler.Persistance.RecentlyReceivedBuffer;
import org.bainsight.market.Model.Events.TickAcceptedEvent;
import org.bainsight.market.Model.Events.TickReceivedEvent;
import org.bainsight.market.Repository.CandleStickRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;

@Configuration
@RequiredArgsConstructor
public class DisruptorConfig {

    @Value("${exchange.id}")
    private String[] exchanges;

    private CandleHandler candleHandler;

    private TickReceivedEventHandler receivedEventHandler;

    private final static String USER_SERVICE_CHANNEL;
    private final static Integer USER_SERVICE_STREAM_ID;

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

        this.candleHandler = new CandleHandler(
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

        disruptor.handleEventsWith(candleHandler, new MarketAnalyzer((byte) 0, (byte) 1));

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



    @Scheduled(cron = "0 * 9-23 * * *")
    public void takeSnapshot(){
        if(!LeaderConfig.IS_LEADER.get()) return;
        this.candleHandler.takeSnapshot();
    }



    @Scheduled(cron = "0 0 16 * * *")
    public void reset(){
        try{ this.receivedEventHandler.reset(); }
        catch (Exception e){ System.out.println(e.getMessage()); }
    }

    @Scheduled(fixedRate = 1000)
    public void checkIfRequireRecovery(){
        this.receivedEventHandler.requireRecovery();
    }


    @Value("${spring.profiles.active}")
    private String[] profiles;
    public TickReceivedEventHandler getReceivedEventHandler() {
        for(String profile : profiles){
            if(profile.equals("test")) return this.receivedEventHandler;
        }
        return null;
    }

    public CandleHandler getCandleHandler() {
        for(String profile : profiles){
            if(profile.equals("test")) return this.candleHandler;
        }
        return null;
    }
}
