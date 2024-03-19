package org.bainsight.liquidity.Config.Disruptor;


import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.bainsight.liquidity.Config.Disruptor.ThreadFactory.TickEventFactory;
import org.bainsight.liquidity.Handler.Event.CandleHandler;
import org.bainsight.liquidity.Handler.Event.MarketAnalyzer;
import org.bainsight.liquidity.Handler.Event.TickReceivedEventHandler;
import org.bainsight.liquidity.Handler.Exception.TickExceptionHandler;
import org.bainsight.liquidity.Handler.Persistance.CandleManagerBuffer;
import org.bainsight.liquidity.Handler.Persistance.MessageBufferManager;
import org.bainsight.liquidity.Model.Events.TickAcceptedEvent;
import org.bainsight.liquidity.Model.Events.TickReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ExecutorService;

@Configuration
public class DisruptorConfig {

    @Value("${exchange.id}")
    private String[] exchanges;
    @Bean
    public Disruptor<TickReceivedEvent> tickReceivedEventDisruptor(final MessageBufferManager messageBufferManager,
                                                                   final RingBuffer<TickAcceptedEvent> acceptedBuffer,
                                                                   final ExecutorService recoveryExecutor) {
        Disruptor<TickReceivedEvent> disruptor = new Disruptor<>(
                TickReceivedEvent.TICK_RECEIVED_EVENT_FACTORY,
                2048,
                TickEventFactory.INSTANCE,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
                );

        disruptor.handleEventsWith(new TickReceivedEventHandler(messageBufferManager, acceptedBuffer, recoveryExecutor, exchanges));

        disruptor.setDefaultExceptionHandler(new TickExceptionHandler<>());

        disruptor.start();


        return disruptor;
    }

    @Bean
    public Disruptor<TickAcceptedEvent> tickAcceptedEventDisruptor(final RedisTemplate<String, Object> redisTemplate,
                                                                   final CandleManagerBuffer candleManagerBuffer) {
        Disruptor<TickAcceptedEvent> disruptor = new Disruptor<>(
                TickAcceptedEvent.TICK_ACCEPTED_EVENT_FACTORY,
                1024,
                TickEventFactory.INSTANCE,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );

        disruptor.setDefaultExceptionHandler(new TickExceptionHandler<>());
        disruptor.handleEventsWith(new CandleHandler((byte) 0, (byte) 1, redisTemplate, candleManagerBuffer),
                                   new MarketAnalyzer((byte) 0, (byte) 1));

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
}
