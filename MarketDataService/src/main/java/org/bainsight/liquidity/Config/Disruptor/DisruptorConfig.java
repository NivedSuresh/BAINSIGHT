package org.bainsight.liquidity.Config.Disruptor;


import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.aeron.Aeron;
import io.aeron.Publication;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bainsight.liquidity.Config.Disruptor.ThreadFactory.TickEventFactory;
import org.bainsight.liquidity.Config.Election.LeaderConfig;
import org.bainsight.liquidity.Handler.Event.CandleHandler;
import org.bainsight.liquidity.Handler.Event.MarketAnalyzer;
import org.bainsight.liquidity.Handler.Event.TickReceivedEventHandler;
import org.bainsight.liquidity.Handler.Exception.TickExceptionHandler;
import org.bainsight.liquidity.Handler.Persistance.CandleStickBuffer;
import org.bainsight.liquidity.Handler.Persistance.RecentlyReceivedBuffer;
import org.bainsight.liquidity.Model.Events.TickAcceptedEvent;
import org.bainsight.liquidity.Model.Events.TickReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Configuration
@RequiredArgsConstructor
public class DisruptorConfig {

    @Value("${exchange.id}")
    private String[] exchanges;


    private CandleHandler candleHandler;

    private TickReceivedEventHandler receivedEventHandler;


    public static final int STICK_STREAM_ID;
    public static final String STICK_CHANNEL;

    static {
        STICK_STREAM_ID = Integer.getInteger("");
        STICK_CHANNEL = System.getProperty("");
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

        this.receivedEventHandler = new TickReceivedEventHandler(recentlyReceivedBuffer, acceptedBuffer, recoveryExecutor, exchanges, profiles);
        disruptor.handleEventsWith(receivedEventHandler);

        disruptor.setDefaultExceptionHandler(new TickExceptionHandler<>());

        disruptor.start();


        return disruptor;
    }

    @Bean
    public Disruptor<TickAcceptedEvent> tickAcceptedEventDisruptor(final CandleStickBuffer candleStickBuffer,
                                                                   final Aeron aeron) {
        Disruptor<TickAcceptedEvent> disruptor = new Disruptor<>(
                TickAcceptedEvent.TICK_ACCEPTED_EVENT_FACTORY,
                1024,
                TickEventFactory.INSTANCE,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );

        disruptor.setDefaultExceptionHandler(new TickExceptionHandler<>());
        Publication publication = aeron.addPublication(STICK_CHANNEL, STICK_STREAM_ID);
        this.candleHandler = new CandleHandler((byte) 0, (byte) 1, candleStickBuffer, profiles, publication);

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
